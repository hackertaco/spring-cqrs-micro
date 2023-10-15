package cqrs.microservice.order.events;

import cqrs.microservice.order.domain.OrderStatus;

import java.time.LocalDateTime;

public record OrderCreatedEvent(
        String id,
        String userEmail,
        String userName,
        String deliveryAddress,
        OrderStatus status,
        LocalDateTime deliveryDate) {


}
