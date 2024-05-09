package com.warehouse.management.controller;

import com.warehouse.management.Constants;
import com.warehouse.management.exception.CustomException;
import com.warehouse.management.model.*;
import com.warehouse.management.model.Error;
import com.warehouse.management.service.ItemService;
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
import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger = LogManager.getLogger(OrderController.class);

    @Autowired
    JwtService jwtService;

    @Autowired
    OrderService orderService;

    @Autowired
    UserService userService;

    @Autowired
    ItemService itemService;

    @PostMapping
    public ResponseEntity<Object> createOrder(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Order order) {
        String token = authorizationHeader.replace("Bearer ", "");
        String username = jwtService.extractUsername(token);
        order.setStatus(Status.CREATED);
        UUID uuid = UUID.randomUUID();
        order.setOrder_number(Math.abs(uuid.hashCode() & Integer.MAX_VALUE));
        try {
            logger.info("Creating order for user: {}", username);
            orderService.saveOrder(order, username);


            logger.info("Order created successfully for user: {}", username);

                return ResponseEntity.ok(order );
        }catch (CustomException e) {
            Error error = e.getError();
            logger.error(e.getMessage(), error.getReason(), e.getMessage());
            return ResponseEntity.status(e.getError().getCode()).body(error);
        }

        catch (Exception e) {
            logger.error("Failed to create order for user: {}. Error: {}", username, e.getMessage());
            Error error = new Error();
            error.setCode(500);
            error.setMessage("Failed to create order");
            error.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getOrder(@RequestHeader("Authorization") String authorizationHeader, @RequestParam(required = false) String status) {
        String token = authorizationHeader.replace("Bearer ", "");
        String username = jwtService.extractUsername(token);

        try {
            logger.info("Retrieving orders for user: {}", username);
            User user = userService.findByUsername(username);
            List<Object[]> orders;
            if (user.getRole() == Role.WAREHOUSE_MANAGER) {
                logger.info("Orders retrieved successfully for user: {}", username);
                return new ResponseEntity<>(orderService.getAllOrdersSummary(), HttpStatus.OK);
            } else {
                orders = orderService.getOrders(username, status);
            }
            logger.info("Orders retrieved successfully for user: {}", username);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve orders for user: {}. Error: {}", username, e.getMessage());
            Error error = new Error();
            error.setCode(500);
            error.setMessage("Failed to retrieve orders");
            error.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }



    @PatchMapping("")
    public ResponseEntity<Object> patchOrder(@RequestHeader("Authorization") String authorizationHeader, @RequestParam(required = false) Integer orderId, @RequestParam(required = false) String status, @RequestBody(required = false) Order requestOrder) {
        String token = authorizationHeader.replace("Bearer ", "");
        String username = jwtService.extractUsername(token);
        try {
            logger.info("Updating order for user: {}", username);
            Order order = orderService.getOrderByOrderNumber(username, orderId);
            if (order == null) {
                throw new CustomException(400, "Bad Request", Constants.ORDER_NOT_FOUND);
            }
            orderService.patchOrder(order, status, requestOrder, username);
            logger.info("Order updated successfully for user: {}", username);
            return ResponseEntity.ok().build();
        }catch (CustomException e) {
            Error error = e.getError();
            logger.error(e.getMessage(), error.getReason(), e.getMessage());
            return ResponseEntity.status(e.getError().getCode()).body(error);
        }
        catch (Exception e) {
            logger.error("Failed to update order for user: {}. Error: {}", username, e.getMessage());
            Error error = new Error();
            error.setCode(500);
            error.setMessage("Failed to update order");
            error.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PatchMapping("/approve")
    public ResponseEntity<Object> approveOrder(@RequestParam(required = false) Integer orderId, @RequestParam(required = false) String status, @RequestParam(required = false) String reason) {
        try {
            logger.info("Approving order: {}", orderId);
            Order order = orderService.retrieveOrder(orderId);
            if (order == null) {
                return ResponseEntity.ok().body("[]");
            }

            orderService.approveOrder(order, status,reason);
            logger.info("Order approved successfully: {}", orderId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to approve order for user: {}. Error: {}", orderId, e.getMessage());
            Error error = new Error();
            error.setCode(500);
            error.setMessage("Failed to approve order");
            error.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Object> retrieveOrder(@PathVariable Integer orderId) {
        try {
            logger.info("Retrieving order with ID: {}", orderId);
            Order order = orderService.retrieveOrder(orderId);
            logger.info("Order retrieved successfully with ID: {}", orderId);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve order with ID: {}. Error: {}", orderId, e.getMessage());
            Error error = new Error();
            error.setCode(500);
            error.setMessage("Failed to retrieve order");
            error.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
