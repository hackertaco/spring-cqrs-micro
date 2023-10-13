package cqrs.microservice.order.commands;

import cqrs.microservice.order.domain.OrderStatus;
import cqrs.microservice.shared.commands.BaseCommand;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class UpdateOrderStatusCommand extends BaseCommand {
    @NotNull
    private OrderStatus status;
}
