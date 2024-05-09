package com.warehouse.management.repository;

import com.warehouse.management.model.Delivery;
import com.warehouse.management.model.DeliveryStatus;
import com.warehouse.management.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
   List<Delivery> findByOrderId(Integer orderId);

   List<Delivery> findByStatus(DeliveryStatus status);

}
