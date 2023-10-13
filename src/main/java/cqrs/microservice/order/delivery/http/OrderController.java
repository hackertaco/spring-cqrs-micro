package cqrs.microservice.order.delivery.http;

import cqrs.microservice.order.commands.CommandHandler;
import cqrs.microservice.order.commands.CreateOrderCommand;
import cqrs.microservice.order.domain.OrderStatus;
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

    @GetMapping
    public ResponseEntity<String> getUser(@RequestParam(name = "name", required = false, defaultValue = "Taco")
                                          String name){
        log.info("(GetUser) name: {}", name);
        return ResponseEntity.ok(name);
    }
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateOrderCommand> createOrder(@RequestBody CreateOrderCommand command){
        command.setId(UUID.randomUUID().toString());
        command.setStatus(OrderStatus.NEW);
        log.info("CreateOrderCommand: {}", command);
        return ResponseEntity.status(HttpStatus.CREATED).body(command);
    }
}
