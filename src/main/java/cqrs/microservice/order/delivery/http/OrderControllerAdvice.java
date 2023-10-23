package cqrs.microservice.order.delivery.http;

import cqrs.microservice.order.exceptions.NotFoundExceptionResponse;
import cqrs.microservice.order.exceptions.OrderNotFoundException;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.annotation.NewSpan;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.ZonedDateTime;

import java.util.Optional;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
@Order(1)
public class OrderControllerAdvice {
    private final ObjectProvider<Tracer> tracer;

    @ExceptionHandler(OrderNotFoundException.class)
    @NewSpan(name = "OrderControllerAdvice")
    public ResponseEntity<NotFoundExceptionResponse> handleOrderNotFoundException(OrderNotFoundException ex, WebRequest request){
        final var response = NotFoundExceptionResponse.builder()
                .message(ex.getMessage())
                .status(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .timestamp(ZonedDateTime.from(LocalDateTime.now()))
                .build();
        log.error("OrderNotFoundException response: {} ", response);
        Optional.ofNullable(tracer.getIfAvailable().currentSpan()).map(span -> span.error(ex));


        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
