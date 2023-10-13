package cqrs.microservice.order.queries;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetOrderByIdQuery {
    @NotBlank
    private String id;
}
