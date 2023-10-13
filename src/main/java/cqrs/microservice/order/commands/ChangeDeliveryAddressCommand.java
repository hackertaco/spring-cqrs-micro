package cqrs.microservice.order.commands;

import cqrs.microservice.shared.BaseCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class ChangeDeliveryAddressCommand extends BaseCommand {
    private String deliveryAddress;
}
