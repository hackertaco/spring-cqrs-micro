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

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class CreateOrderCommand extends BaseCommand {
    @NotBlank
    @Size(min = 10, message = "invalid min length")
    @Size(max = 500, message = "max length is 500")
    private String userEmail;
    @NotBlank
    @Size(min = 10, message = "invalid min length")
    @Size(max = 500, message = "max length is 500")
    private String userName;
    @NotBlank
    @Size(min = 10, message = "invalid min length")
    @Size(max = 500, message = "max length is 500")
    private String deliveryAddress;
    @NotNull
    private OrderStatus status;
    @NotNull
    private ZonedDateTime deliveryDate;
}
