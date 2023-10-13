package cqrs.microservice.order.commands;

import cqrs.microservice.order.domain.OrderStatus;
import cqrs.microservice.shared.commands.BaseCommand;
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
    private String email;
    private String userEmail;
    private String userName;
    private String deliveryAddress;
    private OrderStatus status;
    private ZonedDateTime deliveryDate;
}
