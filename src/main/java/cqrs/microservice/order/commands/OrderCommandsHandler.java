package cqrs.microservice.order.commands;

import cqrs.microservice.configuration.OrderKafkaTopicsConfiguration;
import cqrs.microservice.order.domain.OrderStatus;
import cqrs.microservice.order.events.OrderCreatedEvent;
import cqrs.microservice.order.events.OrderDeliveryAddressChangedEvent;
import cqrs.microservice.order.events.OrderStatusUpdatedEvent;
import cqrs.microservice.order.exceptions.OrderNotFoundException;
import cqrs.microservice.mappers.OrderMapper;
import cqrs.microservice.order.repository.OrderPostgresRepository;
import cqrs.microservice.shared.serializer.JsonSerializer;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import io.micrometer.tracing.annotation.NewSpan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderCommandsHandler implements CommandHandler{
    private final OrderPostgresRepository postgresRepository;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final OrderKafkaTopicsConfiguration orderKafkaTopicsConfiguration;
    private final JsonSerializer jsonSerializer;
    private final ObjectProvider<Tracer> tracer;


    @Override
    @NewSpan(name = "(CreateOrderCommand)")
    public String handle(CreateOrderCommand command) {
        Optional.ofNullable(tracer.getIfAvailable().currentSpan()).ifPresent(span -> span.tag("CreateOrderCommand", command.toString()));
        final var order = OrderMapper.orderFromCreateOrderCommand(command);
        final var savedOrder = postgresRepository.save(order);
        final var event = OrderMapper.orderCreatedEventFromOrder(order);
        publishMessage(orderKafkaTopicsConfiguration.getOrderCreatedTopic(), savedOrder, null);
        Optional.ofNullable(tracer.getIfAvailable().currentSpan()).ifPresent(span -> span.tag("savedOrder", savedOrder.toString()));
        log.info("savedOrder: {}", savedOrder);
        return savedOrder.getId().toString();
    }

    @Override
    @Transactional
    @NewSpan(name = "(UpdateOrderStatusCommand)")
    public void handle(UpdateOrderStatusCommand command) {
        Optional.ofNullable(tracer.getIfAvailable().currentSpan()).ifPresent(span -> span.tag("UpdateOrderStatusCommand", command.toString()));
        final var orderOptional = postgresRepository.findById(UUID.fromString(command.getId()));
        if(orderOptional.isEmpty()) throw new OrderNotFoundException("order not found: " + command.getId());

        final var order = orderOptional.get();
        order.setStatus(OrderStatus.valueOf(command.getStatus().toString()));
        order.setUpdatedAt(LocalDateTime.now());
        postgresRepository.save(order);
        final var event = new OrderStatusUpdatedEvent(order.getId().toString(),order.getStatus());
        publishMessage(orderKafkaTopicsConfiguration.getOrderStatusUpdatedTopic(), order, Map.of("traceId", UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8)));
        Optional.ofNullable(tracer.getIfAvailable().currentSpan()).ifPresent(span -> span.tag("event", event.toString()));
    }

    @Override
    @Transactional
    @NewSpan(name = "(ChangeDeliveryAddressCommand)")
    public void handle(ChangeDeliveryAddressCommand command) {
        Optional.ofNullable(tracer.getIfAvailable().currentSpan()).ifPresent(span -> span.tag("ChangeDeliveryAddressCommand", command.toString()));
        final var orderOptional = postgresRepository.findById(UUID.fromString(command.getId()));
        if(orderOptional.isEmpty()) throw new OrderNotFoundException("order not found: " + command.getId());

        final var order = orderOptional.get();
        order.setDeliveryAddress(command.getDeliveryAddress());
        order.setUpdatedAt(LocalDateTime.now());
        postgresRepository.save(order);
        final var event = new OrderDeliveryAddressChangedEvent(order.getId().toString(),order.getDeliveryAddress());
        publishMessage(orderKafkaTopicsConfiguration.getOrderAddressChangedTopic(), order, null);
        Optional.ofNullable(tracer.getIfAvailable().currentSpan()).ifPresent(span -> span.tag("event", event.toString()));
    }
    @NewSpan(name = "(publishMessage)")
    private void publishMessage(String topic, Object data, Map<String, byte[]> headers){
        try {
            byte[] bytes = jsonSerializer.serializeToJsonBytes(data);
            Optional.ofNullable(tracer.getIfAvailable().currentSpan()).ifPresent(span -> span.tag("data", new String(bytes)));
            ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, bytes);

            if(headers != null){
                headers.forEach((key, value) -> record.headers().add(key, value));
            }
            kafkaTemplate.send(record).get(1000, TimeUnit.MILLISECONDS);
            log.info("send success: {}", record);
            Optional.ofNullable(tracer.getIfAvailable().currentSpan()).map(span -> span.tag("record", record.toString()));
        } catch (Exception e){
            log.error("publishMessage error: {}", e.getMessage());
            Optional.ofNullable(tracer.getIfAvailable().currentSpan()).ifPresent(span -> span.error(e));
            throw new RuntimeException(e);
        }
    }

}
