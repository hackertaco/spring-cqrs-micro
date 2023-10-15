package cqrs.microservice.order.commands;

import cqrs.microservice.order.domain.OrderStatus;
import cqrs.microservice.shared.commands.BaseCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class CreateOrderCommand extends BaseCommand {
    @NotBlank
    @Size(min = 10, max = 250, message = "invalid min length")
    @Size(message = "max length is 500")
    private String userEmail;
    @NotBlank
    @Size(min = 10, max = 250, message = "invalid userName length")
    private String userName;
    @NotBlank
    @Size(min = 10, max = 250, message = "invalid deliveryAddress length")
    private String deliveryAddress;
    private OrderStatus status;
    @NotNull
    private LocalDateTime deliveryDate;
}
