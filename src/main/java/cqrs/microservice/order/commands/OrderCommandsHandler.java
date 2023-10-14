package cqrs.microservice.order.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cqrs.microservice.order.domain.Order;
import cqrs.microservice.order.domain.OrderStatus;
import cqrs.microservice.order.exceptions.OrderNotFoundException;
import cqrs.microservice.order.mappers.OrderMapper;
import cqrs.microservice.order.repository.OrderPostgresRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderCommandsHandler implements CommandHandler{
    private final OrderPostgresRepository postgresRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;


    @Override
    public String handle(CreateOrderCommand command) {
        final var order = OrderMapper.orderFromCreateOrderCommand(command);
        final var savedOrder = postgresRepository.save(order);
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

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(order);
            ProducerRecord<String, byte[]> record = new ProducerRecord<>("change_delivery_address", bytes);
            record.headers().add("Alex", "PRO".getBytes());
            kafkaTemplate.send(record);
            log.info("kafka send: {}", record);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
