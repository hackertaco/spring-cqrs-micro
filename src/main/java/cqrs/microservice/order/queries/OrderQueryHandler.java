package cqrs.microservice.order.queries;

import cqrs.microservice.order.domain.OrderDocument;
import cqrs.microservice.order.dto.OrderResponseDto;
import cqrs.microservice.order.exceptions.OrderNotFoundException;
import cqrs.microservice.order.mappers.OrderMapper;
import cqrs.microservice.order.repository.OrderMongoRepository;
import cqrs.microservice.order.repository.OrderPostgresRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderQueryHandler implements QueryHandler{
    private final OrderPostgresRepository postgresRepository;
    private final OrderMongoRepository mongoRepository;

    @Override
    public OrderResponseDto handle(GetOrderByIdQuery query) {
        final var document = mongoRepository.findById(query.getId());
        if(document.isPresent()){
            return OrderMapper.orderResponseDtoFromDocument(document.get());
        }
        final var order = postgresRepository.findById(UUID.fromString(query.getId()));
        if(order.isEmpty()) throw new OrderNotFoundException("order not found: "+ query.getId());

        OrderDocument orderDocument = OrderMapper.orderDocumentFromEntity(order.get());
        mongoRepository.save(orderDocument);
        return OrderMapper.orderResponseDtoFromEntity(order.get());
    }
}
