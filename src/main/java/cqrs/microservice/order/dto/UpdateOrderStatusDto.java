package cqrs.microservice.order.dto;

import cqrs.microservice.order.domain.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record UpdateOrderStatusDto(
        @NotNull OrderStatus status
) {
}
