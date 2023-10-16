package cqrs.microservice.order.queries;

import jakarta.validation.constraints.NotBlank;


public record GetOrderByIdQuery(@NotBlank String id) {

}
