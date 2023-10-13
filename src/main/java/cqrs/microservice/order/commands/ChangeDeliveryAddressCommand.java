package cqrs.microservice.order.commands;

import cqrs.microservice.shared.commands.BaseCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class ChangeDeliveryAddressCommand extends BaseCommand {
    @NotBlank
    @Size(min = 10, message = "deliveryAddress should be at least 10 characters")
    @Size(max = 500, message = "deliveryAddress should be at most 500 characters")
    private String deliveryAddress;
}
