package com.warehouse.management.controller;

import com.warehouse.management.Constants;
import com.warehouse.management.exception.CustomException;
import com.warehouse.management.model.Error;
import com.warehouse.management.model.Truck;
import com.warehouse.management.model.TruckStatus;
import com.warehouse.management.service.TruckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/truck")
public class TruckController {

    private static final Logger logger = LoggerFactory.getLogger(TruckController.class);

    @Autowired
    private TruckService truckService;

    @PostMapping
    public ResponseEntity<Object> createTruck(@RequestBody Truck truck) {
        try {
            truck.setTruckStatus(TruckStatus.AVAILABLE);
            logger.info("Creating truck: {}", truck.getChassisNumber());
            if(truckService.getTruckByChassisNumber(truck.getChassisNumber()) != null){
                throw new CustomException(400, "Bad Request", Constants.TRUCK_ALREADY_EXISTS);
            }
            Truck createdTruck = truckService.createTruck(truck);
            logger.info("Truck created successfully: {}", truck.getChassisNumber());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTruck);
        }
        catch (CustomException e) {
            Error error = e.getError();
            logger.error(e.getMessage(), error.getReason(), e.getMessage());
            return ResponseEntity.status(e.getError().getCode()).body(error);
        }
        catch (Exception e) {
            Error error = new Error();
            error.setMessage(Constants.ERROR);
            error.setCode(500);
            logger.error("Error occurred while creating truck: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{chassisNumber}")
    public ResponseEntity<Object> getTruckByChassisNumber(@PathVariable String chassisNumber) {
        try {
            logger.info("Retrieving truck with chassis number: {}", chassisNumber);
            Truck truck = truckService.getTruckByChassisNumber(chassisNumber);
            if (truck != null) {
                logger.info("Truck retrieved successfully: {}", chassisNumber);
                return ResponseEntity.ok(truck);
            } else {
                logger.warn("Truck not found with chassis number: {}", chassisNumber);
                return ResponseEntity.ok().body("[]");
            }
        } catch (Exception e) {
            Error error = new Error();
            error.setMessage(Constants.ERROR);
            error.setCode(500);
            logger.error("Error occurred while retrieving truck: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllTrucks() {
        try {
            logger.info("Retrieving all trucks");
            List<Truck> trucks = truckService.getAllTrucks();
            logger.info("Trucks retrieved successfully");
            return ResponseEntity.ok(trucks);
        } catch (Exception e) {
            Error error = new Error();
            error.setMessage(Constants.ERROR);
            error.setCode(500);
            logger.error("Error occurred while retrieving all trucks: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("")
    public ResponseEntity<Object> updateTruck(@RequestBody Truck updatedTruck) {
        try {
            String chassisNumber = updatedTruck.getChassisNumber();
            updatedTruck.setTruckStatus(TruckStatus.AVAILABLE);
            logger.info("Updating truck with chassis number: {}", chassisNumber);
            Truck truck = truckService.updateTruck(chassisNumber, updatedTruck);
            if (truck != null) {
                logger.info("Truck updated successfully: {}", chassisNumber);
                return ResponseEntity.ok(truck);
            } else {
                Error error = new Error();
                error.setMessage(Constants.ERROR);
                error.setCode(500);
                logger.warn("Truck not found with chassis number: {}", chassisNumber);
                return ResponseEntity.ok().body("[]");
            }
        } catch (Exception e) {
            logger.error("Error occurred while updating truck: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{chassisNumber}")
    public ResponseEntity<Object> deleteTruck(@PathVariable String chassisNumber) {
        try {
            logger.info("Deleting truck with chassis number: {}", chassisNumber);
            boolean deleted = truckService.deleteTruck(chassisNumber);
            if (deleted) {
                logger.info("Truck deleted successfully: {}", chassisNumber);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Truck not found with chassis number: {}", chassisNumber);
                return ResponseEntity.ok().body("[]");
            }
        } catch (Exception e) {
            Error error = new Error();
            error.setMessage(Constants.ERROR);
            error.setCode(500);
            logger.error("Error occurred while deleting truck: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
