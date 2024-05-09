package com.warehouse.management.service;

import com.warehouse.management.Constants;
import com.warehouse.management.exception.CustomException;
import com.warehouse.management.model.*;
import com.warehouse.management.repository.DeliveryRepository;
import com.warehouse.management.repository.OrderRepository;
import com.warehouse.management.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final InventoryItemService inventoryItemService;
    private final DeliveryRepository deliveryRepository;


    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ItemService itemService, InventoryItemService inventoryItemService, DeliveryRepository deliveryRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.itemService = itemService;
        this.inventoryItemService = inventoryItemService;
        this.deliveryRepository = deliveryRepository;
    }

    public List<Object[]> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public List<Object[]> getOrders(String username, String status) {
        if (status != null && !status.equalsIgnoreCase("")) {
            return orderRepository.findOrdersByStatus(username, Status.valueOf(status.toUpperCase()));
        } else {
            return orderRepository.findOrders(username);
        }
    }

    public void patchOrder(Order order, String requestStatus, Order requestOrder, String username) throws CustomException {
        Status status = Status.fromString(requestStatus);
        Status orderStatus = order.getStatus();

        if (status == Status.CANCELED && !(orderStatus == Status.FULFILLED || orderStatus == Status.UNDER_DELIVERY || orderStatus == Status.CANCELED)) {
            order.setStatus(orderStatus);
        }
        else if ((orderStatus == Status.CREATED || orderStatus == Status.DECLINED) && status == Status.AWAITING_APPROVAL) {
            order.setStatus(status);
        }
        else if ((orderStatus == Status.CREATED || orderStatus == Status.DECLINED) && requestOrder != null) {
            for (Item item : order.getItems()) {
                item.setOrder(order);
                itemService.deleteItem(item.getId());
            }
            for (Item item : requestOrder.getItems()) {
                item.setOrder(order);
                itemService.saveOrUpdateItem(item);
            }
            order.setItems(requestOrder.getItems());
        }
        saveOrder(order, username);
    }

    public void approveOrder(Order order, String requestStatus, String reason) throws CustomException {
        Status status = Status.fromString(requestStatus);
        Status orderStatus = order.getStatus();
        String username = order.getUsername();

        if (orderStatus == Status.AWAITING_APPROVAL && (status == Status.APPROVED || status == Status.DECLINED)) {
            order.setStatus(Status.valueOf(requestStatus));
            if(reason != null){
                order.setReason(reason);
            }
        } else if (orderStatus == Status.UNDER_DELIVERY && status == Status.FULFILLED) {
            List<Delivery> deliveries = deliveryRepository.findByOrderId(order.getOrder_number());
            for (Delivery delivery : deliveries) {
                if (delivery.getStatus() != DeliveryStatus.DELIVERED)
                    throw new CustomException(400, "Bad Request", Constants.DELIVERY_NOT_COMPLETED);
            }
            order.setStatus(Status.FULFILLED);
        }
        saveOrder(order, username);
    }

    public Order getOrderByOrderNumber(String username, Integer orderNumber) {
        return orderRepository.findOrdersByOrderNumber(username, orderNumber);
    }

    public Order retrieveOrder(Integer orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    public void saveOrder(Order order, String username) throws CustomException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            inventoryItemService.checkInventory(order.getItems());
            User user = optionalUser.get();
            order.setUser(user);
            orderRepository.save(order);
            for (Item item : order.getItems()) {
                item.setOrder(order);
                itemService.saveOrUpdateItem(item);
            }
        } else {
            throw new CustomException(400,"Bad Request", Constants.USER_NOT_FOUND);
        }
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public List<OrderSummary> getAllOrdersSummary() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(order -> new OrderSummary(order.getOrder_number(), order.getUser().getUsername(), order.getStatus()))
                .collect(Collectors.toList());
    }
}
