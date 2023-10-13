package cqrs.microservice.order.queries;

import cqrs.microservice.order.dto.OrderResponseDto;

import java.util.Optional;

public interface QueryHandler {
    OrderResponseDto handle(GetOrderByIdQuery query);
}
