package com.warehouse.management.controller;

import com.warehouse.management.model.DeliveryStatus;
import com.warehouse.management.model.Order;
import com.warehouse.management.model.Role;
import com.warehouse.management.model.User;
import com.warehouse.management.service.DeliveryService;
import com.warehouse.management.service.JwtService;
import com.warehouse.management.service.OrderService;
import com.warehouse.management.service.UserService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {


    private final DeliveryService deliveryService;

    public TestController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("")
    public ResponseEntity<Object> retrieveOrder() {

        deliveryService.checkDeliveries();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve order");
        }
    @PatchMapping("")
    public ResponseEntity<Object> test() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve order");
    }
    }

