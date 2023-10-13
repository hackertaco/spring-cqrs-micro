package cqrs.microservice.order.repository;

import cqrs.microservice.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderPostgresRepository extends JpaRepository<Order, UUID> {

}
