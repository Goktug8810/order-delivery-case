package com.goktug.order_delivery.repository;

import com.goktug.order_delivery.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    @Query("SELECT o FROM OrderEntity o " +
            "WHERE o.deliveredAt IS NOT NULL " +
            "AND FUNCTION('DATE', o.deliveredAt) = :deliveryDate")
    List<OrderEntity> findDeliveredOrdersByDate(@Param("deliveryDate") LocalDate deliveryDate);
}