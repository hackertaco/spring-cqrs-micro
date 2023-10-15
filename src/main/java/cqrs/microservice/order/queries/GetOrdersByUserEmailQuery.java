package cqrs.microservice.order.queries;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GetOrdersByUserEmailQuery(
        @NotBlank @Size(min = 5, max = 250, message = "invalid email length") String userEmail,
        int page,
        int size
) {
}
