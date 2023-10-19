package cqrs.microservice.order.events;

import cqrs.microservice.order.exceptions.OrderNotFoundException;
import cqrs.microservice.order.mappers.OrderMapper;
import cqrs.microservice.order.repository.OrderMongoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.annotation.NewSpan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
@AllArgsConstructor
public class OrderEventsHandler implements EventsHandler{
    private final OrderMongoRepository orderMongoRepository;
    private final ObjectProvider<Tracer> tracer;


    @Override
    @NewSpan(name = "(OrderCreatedEvent)")
    public void handle(OrderCreatedEvent event) {
        Optional.ofNullable(tracer.getIfAvailable().currentSpan()).map(span -> span.tag("event", event.toString()));
        final var document = OrderMapper.orderDocumentFromCreatedEvent(event);
        final var insert = orderMongoRepository.insert(document);
        log.info("created mongodb order: {}", insert);
        Optional.ofNullable(tracer.getIfAvailable().currentSpan()).map(span -> span.tag("insert", insert.toString()));
    }

    @Override
    @NewSpan(name = "(OrderStatusUpdatedEvent)")
    public void handle(OrderStatusUpdatedEvent event) {
        final var document = orderMongoRepository.findById(event.id());
        if(document.isEmpty()) throw new OrderNotFoundException("order not found exception");

        document.get().setStatus(event.status());
        orderMongoRepository.save(document.get());
        Optional.ofNullable(tracer.getIfAvailable().currentSpan()).map(span -> span.tag("event", event.toString()));
    }

    @Override
    @NewSpan(name = "(OrderDeliveryAddressChangedEvent)")
    public void handle(OrderDeliveryAddressChangedEvent event) {
        final var document = orderMongoRepository.findById(event.id());
        if(document.isEmpty()) throw new OrderNotFoundException("order not found exception");

        document.get().setDeliveryAddress(event.deliveryAddress());
        orderMongoRepository.save(document.get());
        Optional.ofNullable(tracer.getIfAvailable().currentSpan()).map(span -> span.tag("event", event.toString()));
    }
}
