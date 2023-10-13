package cqrs.microservice.order.mappers;

import cqrs.microservice.order.domain.Order;
import cqrs.microservice.order.dto.OrderResponseDto;

public final class OrderMapper {
    public static OrderResponseDto orderResponseDtoFromEntity(Order order){
        return OrderResponseDto.builder()
                .id(order.getId().toString())
                .userEmail(order.getUserEmail())
                .userName(order.getUserName())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryDate(order.getDeliveryDate())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .status(order.getStatus().name())
                .build();
    }
}
