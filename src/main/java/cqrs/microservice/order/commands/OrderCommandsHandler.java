package cqrs.microservice.order.commands;

import cqrs.microservice.order.domain.Order;
import cqrs.microservice.order.repository.OrderPostgresRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderCommandsHandler implements CommandHandler{
    private final OrderPostgresRepository postgresRepository;


    @Override
    public String handle(CreateOrderCommand command) {
        final var order = Order.builder()
                .id(UUID.fromString(command.getId()))
                .userEmail(command.getUserEmail())
                .userName(command.getUserName())
                .status(command.getStatus())
                .deliveryAddress(command.getDeliveryAddress())
                .deliveryDate(command.getDeliveryDate())
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
        final var savedOrder = postgresRepository.save(order);
        log.info("savedOrder: {}", savedOrder);
        return savedOrder.getId().toString();
    }
}
