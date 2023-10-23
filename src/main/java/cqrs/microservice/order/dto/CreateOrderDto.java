package cqrs.microservice.order.dto;

import cqrs.microservice.order.domain.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record CreateOrderDto(
        @NotBlank @Size(min = 5, max = 250,message="invalid userEmail length") String userEmail,
        @NotBlank @Size(min = 5, max = 250,message="invalid userName length") String userName,
        @NotBlank @Size(min = 5, max = 250,message="invalid deliveryAddress length") String deliveryAddress,
        OrderStatus status,
        LocalDateTime deliveryDate
) {
}
