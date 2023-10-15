package cqrs.microservice.order.repository;

import cqrs.microservice.order.domain.OrderDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderMongoRepository extends MongoRepository<OrderDocument, String> {
}
