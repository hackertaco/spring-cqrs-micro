package cqrs.microservice.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeDeliveryAddressDto(
        @NotBlank
        @Size(min = 10, message="delivery address should be at least 10 chnaracters")
        @Size(max = 500, message="delivery address should not be greater than 500 chnaracters")
        String deliveryAddress
) {
}
