package cqrs.microservice.order.commands;

public interface CommandHandler {
    public String handle(CreateOrderCommand command);
    public void handle(UpdateOrderStatusCommand command);
    public void handle(ChangeDeliveryAddressCommand command);
}
