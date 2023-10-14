package cqrs.microservice.order.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import cqrs.microservice.configuration.OrderKafkaTopicsConfiguration;
import cqrs.microservice.order.domain.OrderStatus;
import cqrs.microservice.order.events.OrderCreatedEvent;
import cqrs.microservice.order.events.OrderDeliveryAddressChangedEvent;
import cqrs.microservice.order.events.OrderStatusUpdatedEvent;
import cqrs.microservice.order.exceptions.OrderNotFoundException;
import cqrs.microservice.order.mappers.OrderMapper;
import cqrs.microservice.order.repository.OrderPostgresRepository;
import cqrs.microservice.shared.serializer.JsonSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderCommandsHandler implements CommandHandler{
    private final OrderPostgresRepository postgresRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final OrderKafkaTopicsConfiguration orderKafkaTopicsConfiguration;
    private final JsonSerializer jsonSerializer;


    @Override
    public String handle(CreateOrderCommand command) {
        final var order = OrderMapper.orderFromCreateOrderCommand(command);
        final var savedOrder = postgresRepository.save(order);
        final var event = new OrderCreatedEvent(order.getUserEmail(), order.getUserName(), order.getDeliveryAddress(), order.getStatus(), order.getDeliveryDate());
        publishMessage(orderKafkaTopicsConfiguration.getOrderCreatedTopic(), savedOrder, null);
        log.info("savedOrder: {}", savedOrder);
        return savedOrder.getId().toString();
    }

    @Override
    @Transactional
    public void handle(UpdateOrderStatusCommand command) {
        final var orderOptional = postgresRepository.findById(UUID.fromString(command.getId()));
        if(orderOptional.isEmpty()) throw new OrderNotFoundException("order not found: " + command.getId());

        final var order = orderOptional.get();
        order.setStatus(OrderStatus.valueOf(command.getStatus().toString()));
        order.setUpdatedAt(ZonedDateTime.now());
        postgresRepository.save(order);
        final var event = new OrderStatusUpdatedEvent(order.getStatus());
        publishMessage(orderKafkaTopicsConfiguration.getOrderStatusUpdatedTopic(), order, Map.of("Taco", "PRO".getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    @Transactional
    public void handle(ChangeDeliveryAddressCommand command) {
        final var orderOptional = postgresRepository.findById(UUID.fromString(command.getId()));
        if(orderOptional.isEmpty()) throw new OrderNotFoundException("order not found: " + command.getId());

        final var order = orderOptional.get();
        order.setDeliveryAddress(command.getDeliveryAddress());
        order.setUpdatedAt(ZonedDateTime.now());
        postgresRepository.save(order);
        final var event = new OrderDeliveryAddressChangedEvent(order.getDeliveryAddress());
        publishMessage(orderKafkaTopicsConfiguration.getOrderAddressChangedTopic(), order, null);
    }
    private void publishMessage(String topic, Object data, Map<String, byte[]> headers){
        try {
            byte[] bytes = jsonSerializer.serializeToJsonBytes(data);
            ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, bytes);

            if(headers != null){
                headers.forEach((key, value) -> record.headers().add(key, value));
            }
            kafkaTemplate.send(record).get(1000, TimeUnit.MILLISECONDS);
            log.info("send success: {}", record);
        } catch (Exception e){
            log.error("publishMessage error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
