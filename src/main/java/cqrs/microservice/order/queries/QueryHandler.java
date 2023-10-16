package cqrs.microservice.order.queries;

import cqrs.microservice.order.domain.OrderDocument;
import cqrs.microservice.order.dto.OrderResponseDto;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface QueryHandler {
    OrderResponseDto handle(GetOrderByIdQuery query);
    Page<OrderDocument> handle(GetOrdersByUserEmailQuery query);
    Page<OrderDocument> handle(GetOrdersByStatusQuery query);
}
