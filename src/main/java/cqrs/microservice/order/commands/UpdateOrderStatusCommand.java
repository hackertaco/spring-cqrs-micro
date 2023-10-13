package cqrs.microservice.order.commands;

import cqrs.microservice.order.domain.OrderStatus;
import cqrs.microservice.shared.BaseCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class UpdateOrderStatusCommand extends BaseCommand {
    private OrderStatus status;
}
