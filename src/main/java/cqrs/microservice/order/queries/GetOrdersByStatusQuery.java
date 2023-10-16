package cqrs.microservice.order.queries;

import cqrs.microservice.order.domain.OrderStatus;

public record GetOrdersByStatusQuery(OrderStatus status, int page, int size) {
}
