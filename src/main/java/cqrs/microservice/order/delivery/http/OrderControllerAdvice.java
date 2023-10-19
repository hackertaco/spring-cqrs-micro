package cqrs.microservice.order.delivery.http;

import cqrs.microservice.order.exceptions.NotFoundExceptionResponse;
import cqrs.microservice.order.exceptions.OrderNotFoundException;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class OrderControllerAdvice extends ResponseEntityExceptionHandler {
    private final ObjectProvider<Tracer> tracer;

    @ExceptionHandler(OrderNotFoundException.class)
    @NewSpan(name = "OrderControllerAdvice")
    public ResponseEntity<NotFoundExceptionResponse> handleOrderNotFoundException(OrderNotFoundException ex, WebRequest request){
        final var response = NotFoundExceptionResponse.builder()
                .message(ex.getMessage())
                .status("404")
                .timestamp(ZonedDateTime.now())
                .build();
        log.error("OrderNotFoundException response: {} ", response);
        Optional.ofNullable(tracer.getIfAvailable().currentSpan()).map(span -> span.error(ex));


        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", ZonedDateTime.now());
        body.put("status", status.value());

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        body.put("errors", errors);
        Optional.ofNullable(tracer.getIfAvailable().currentSpan()).map(span -> span.error(ex));


        return new ResponseEntity<>(body, status);
    }
}
