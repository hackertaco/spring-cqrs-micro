package cqrs.microservice.order.delivery.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import cqrs.microservice.configuration.OrderKafkaTopicsConfiguration;
import cqrs.microservice.order.domain.OrderDocument;
import cqrs.microservice.order.events.OrderCreatedEvent;
import cqrs.microservice.order.events.OrderDeliveryAddressChangedEvent;
import cqrs.microservice.order.events.OrderStatusUpdatedEvent;
import cqrs.microservice.order.repository.OrderMongoRepository;
import cqrs.microservice.shared.serializer.JsonSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.annotation.NewSpan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderKafkaListener {
    private final ObjectMapper objectMapper;
    private final JsonSerializer jsonSerializer;
    private final OrderKafkaTopicsConfiguration orderKafkaTopicsConfiguration;
    private final ObjectProvider<Tracer> tracer;

    @KafkaListener(topics = {"${order.kafka.topics.order-address-changed}"}, groupId = "${order.kafka.groupId}", concurrency = "${order.kafka.default-concurrency}")
    @NewSpan(name = "(changeDeliveryAddressListener)")
    public void changeDeliveryAddressListener(
            @Payload byte[] data,
            ConsumerRecordMetadata meta,
            Acknowledgment ack
            ) {
        Optional.ofNullable(tracer.getIfAvailable().currentSpan()).ifPresent(span -> span.tag("data", new String(data)));
        logEvent(data, meta);
        try {
            final var event = jsonSerializer.deserializeFromJsonBytes(data,
                    OrderDeliveryAddressChangedEvent.class);
            ack.acknowledge();
            log.info("ack event: {}", event);
        }  catch (Exception e) {
            ack.nack(Duration.ofMillis(1000));
            Optional.ofNullable(tracer.getIfAvailable().currentSpan()).ifPresent(span -> span.error(e));
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = {"${order.kafka.topics.order-status-updated}"}, groupId = "${order.kafka.groupId}", concurrency = "${order.kafka.default-concurrency}")
    @NewSpan(name = "(updateOrderStatusListener)")
    public void updateOrderStatusListener(@Payload byte[] data,ConsumerRecordMetadata meta, Acknowledgment ack){
        logEvent(data, meta);
        Optional.ofNullable(tracer.getIfAvailable().currentSpan()).ifPresent(span -> span.tag("data", new String(data)));
        try {
            final var event = jsonSerializer.deserializeFromJsonBytes(data,
                    OrderStatusUpdatedEvent.class);
            ack.acknowledge();
            log.info("ack event: {}", event);
        }catch (Exception e){
            ack.nack(Duration.ofMillis(1000));
            Optional.ofNullable(tracer.getIfAvailable().currentSpan()).ifPresent(span -> span.error(e));
            log.error("updateOrderStatusListener: {}", e.getMessage());
        }
    }
    @KafkaListener(topics = {"${order.kafka.topics.order-created}"}, groupId = "${order.kafka.groupId}", concurrency = "10")
    @NewSpan(name = "(createOrderListener)")
    public void createOrderListener(@Payload byte[] data,ConsumerRecordMetadata meta, Acknowledgment ack) {
        logEvent(data, meta);
        Optional.ofNullable(tracer.getIfAvailable().currentSpan()).ifPresent(span -> span.tag("data", new String(data)));
        try {
            final var event = jsonSerializer.deserializeFromJsonBytes(data,
                    OrderCreatedEvent.class);
            ack.acknowledge();
            log.info("ack event: {}", event);
        } catch (Exception e) {
            ack.nack(Duration.ofMillis(1000));
            Optional.ofNullable(tracer.getIfAvailable().currentSpan()).ifPresent(span -> span.error(e));
            log.error("createOrderListener: {}", e.getMessage());
        }
    }
    private void logEvent(byte[] data, ConsumerRecordMetadata meta){
        log.info("topic: {}, partition: {}, timestamp:{}, offset: {}, data: {}", meta.topic(), meta.partition(), meta.timestamp(), meta.offset(), new String(data));
    }
}
