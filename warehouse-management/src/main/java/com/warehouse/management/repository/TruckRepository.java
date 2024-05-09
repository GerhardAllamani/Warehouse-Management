package com.warehouse.management.repository;

import com.warehouse.management.model.Truck;
import com.warehouse.management.model.TruckStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TruckRepository extends JpaRepository<Truck, String> {

    Optional<Truck> findByChassisNumber(String chassisNumber);

    void deleteByChassisNumber(String chassisNumber);

    Truck findByChassisNumberAndTruckStatus(String chassisNumber, TruckStatus truckStatus);




}
