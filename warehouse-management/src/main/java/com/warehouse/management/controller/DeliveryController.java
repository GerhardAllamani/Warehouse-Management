package com.warehouse.management.controller;

import com.warehouse.management.Constants;
import com.warehouse.management.exception.CustomException;
import com.warehouse.management.model.Delivery;
import com.warehouse.management.model.Error;
import com.warehouse.management.service.DeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryController.class);

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping
    public ResponseEntity<Object> getAllDeliveries() {
        logger.info("Fetching all deliveries");
        try {
            List<Delivery> deliveries = deliveryService.getAllDeliveries();
            logger.info("Deliveries fetched successfully");
            return ResponseEntity.ok(deliveries);
        } catch (Exception e) {
            logger.error("Error occurred while fetching deliveries: {}", e.getMessage());
            Error error = new Error();
            error.setMessage("Error occurred while fetching deliveries");
            error.setCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getDeliveryById(@PathVariable Long id) {
        logger.info("Fetching delivery by ID: {}", id);
        try {
            Delivery delivery = deliveryService.getDeliveryById(id);
            logger.info("Delivery fetched successfully: {}", id);
            return ResponseEntity.ok(delivery);
        }catch (CustomException e) {
            Error error = e.getError();
            logger.error(e.getMessage(), error.getReason(), e.getMessage());
            return ResponseEntity.status(e.getError().getCode()).body(error);

        }

        catch (Exception e) {
            logger.error("Error occurred while fetching delivery {}: {}", id, e.getMessage());
            Error error = new Error();
            error.setMessage("Error occurred while fetching delivery");
            error.setCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping
    public ResponseEntity<Object> createDelivery(@RequestBody Delivery delivery) {
        logger.info("Creating a new delivery");
        try {
            deliveryService.scheduleDelivery(delivery);
            logger.info("Delivery created successfully");
            return ResponseEntity.ok(Constants.DELIVERY_CREATED_RESPONSE);
        }catch (CustomException e) {
            Error error = e.getError();
            logger.error(e.getMessage(), error.getReason(), e.getMessage());
            return ResponseEntity.status(e.getError().getCode()).body(error);

        }
        catch (Exception e) {
            logger.error("Error occurred while creating delivery: {}", e.getMessage());
            Error error = new Error();
            error.setMessage("Error occurred while creating delivery");
            error.setCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PatchMapping
    public ResponseEntity<Object> patchDelivery(@RequestParam String deliveryStatus, @RequestParam Long deliveryId) {
        logger.info("Creating a new delivery");
        try {
            Delivery delivery = deliveryService.getDeliveryById(deliveryId);
            deliveryService.save(delivery, deliveryStatus);
            logger.info("Delivery created successfully");
            return ResponseEntity.ok(HttpStatus.CREATED);
        }
        catch (CustomException e) {
            Error error = e.getError();
            logger.error(e.getMessage(), error.getReason(), e.getMessage());
            return ResponseEntity.status(e.getError().getCode()).body(error);

        }
        catch (Exception e) {
            logger.error("Error occurred while creating delivery: {}", e.getMessage());
            Error error = new Error();
            error.setMessage("Error occurred while creating delivery");
            error.setCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

}
