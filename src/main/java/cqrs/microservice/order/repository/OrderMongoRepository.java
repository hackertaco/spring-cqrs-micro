package cqrs.microservice.order.repository;

import cqrs.microservice.order.domain.OrderDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderMongoRepository extends MongoRepository<OrderDocument, String> {
    Page<OrderDocument> findByUserEmailOrderByDeliveryDate(String userEmail, Pageable pageable);

}
