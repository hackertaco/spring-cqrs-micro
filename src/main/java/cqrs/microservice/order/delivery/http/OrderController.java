package cqrs.microservice.order.delivery.http;

import cqrs.microservice.order.commands.ChangeDeliveryAddressCommand;
import cqrs.microservice.order.commands.CommandHandler;
import cqrs.microservice.order.commands.CreateOrderCommand;
import cqrs.microservice.order.commands.UpdateOrderStatusCommand;
import cqrs.microservice.order.domain.OrderStatus;
import cqrs.microservice.order.dto.OrderResponseDto;
import cqrs.microservice.order.queries.GetOrderByIdQuery;
import cqrs.microservice.order.queries.QueryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping(path = "api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final CommandHandler commandHandler;
    private final QueryHandler queryHandler;

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable String id){
        log.info("id: {}", id);
        final var order = queryHandler.handle(new GetOrderByIdQuery(id));
        log.info("order: {}", order);
        return ResponseEntity.ok(order);
    }
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateOrderCommand> createOrder(@RequestBody CreateOrderCommand command){
        command.setId(UUID.randomUUID().toString());
        command.setStatus(OrderStatus.NEW);
        final var id = commandHandler.handle(command);
        log.info("created id: {}", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(command);
    }

    @PutMapping(path = "{id}/address", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changeDeliveryAddress(@RequestBody ChangeDeliveryAddressCommand command,
                                                      @PathVariable String id) {
        command.setId(id);
        commandHandler.handle(command);
        log.info("changed address id :{}, address: {} ", id, command.getDeliveryAddress());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PutMapping(path = "{id}/status", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateOrderStatus(@RequestBody UpdateOrderStatusCommand command,
                                                      @PathVariable String id) {
        command.setId(id);
        commandHandler.handle(command);
        log.info("changed address id :{}, status: {} ", id, command.getStatus());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
