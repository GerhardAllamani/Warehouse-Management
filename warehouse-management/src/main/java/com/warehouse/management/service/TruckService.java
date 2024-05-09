package com.warehouse.management.service;

import com.warehouse.management.exception.CustomException;
import com.warehouse.management.model.Truck;
import com.warehouse.management.model.TruckStatus;
import com.warehouse.management.repository.TruckRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TruckService {

    @Autowired
    private TruckRepository truckRepository;

    public Truck createTruck(Truck truck) {
        return truckRepository.save(truck);
    }

    public void findByChassisNumberAndTruckStatus(Truck truck) throws CustomException {
        Truck response = truckRepository.findByChassisNumberAndTruckStatus(truck.getChassisNumber(), TruckStatus.AVAILABLE);
        if (response == null) {
            throw new CustomException(400, "Bad Request","Truck not available for chassis number: " + truck.getChassisNumber());
        }
    }

    public Truck getTruckByChassisNumber(String chassisNumber) {
        Optional<Truck> optionalTruck = truckRepository.findByChassisNumber(chassisNumber);
        return optionalTruck.orElse(null);
    }

    public List<Truck> getAllTrucks() {
        return truckRepository.findAll();
    }

    public Truck updateTruck(String chassisNumber, Truck updatedTruck) {
        Optional<Truck> optionalTruck = truckRepository.findByChassisNumber(chassisNumber);
        if (optionalTruck.isPresent()) {
            return truckRepository.save(updatedTruck);
        } else {
            return null;
        }
    }

    @Transactional
    public boolean deleteTruck(String chassisNumber) {
        Optional<Truck> optionalTruck = truckRepository.findByChassisNumber(chassisNumber);
        if (optionalTruck.isPresent()) {
            truckRepository.deleteByChassisNumber(chassisNumber);
            return true;
        } else {
            return false;
        }
    }
}
