package cqrs.microservice.order.events;

import cqrs.microservice.order.domain.OrderStatus;

public record OrderStatusUpdatedEvent(OrderStatus status) {
}
