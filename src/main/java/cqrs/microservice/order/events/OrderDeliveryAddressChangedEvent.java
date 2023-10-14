package cqrs.microservice.order.events;

public record OrderDeliveryAddressChangedEvent(String deliveryAddress) {
}
