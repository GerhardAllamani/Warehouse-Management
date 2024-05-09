package com.warehouse.management.service;

import com.warehouse.management.Constants;
import com.warehouse.management.exception.CustomException;
import com.warehouse.management.model.*;
import com.warehouse.management.repository.DeliveryItemRepository;
import com.warehouse.management.repository.DeliveryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryService.class);
    private final DeliveryRepository deliveryRepository;
    private final TruckService truckService;
    private final InventoryItemService inventoryItemService;

    private OrderService orderService;
    private final DeliveryItemRepository deliveryItemRepository;

    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository, TruckService truckService, InventoryItemService inventoryItemService, DeliveryItemRepository deliveryItemRepository) {
        this.deliveryRepository = deliveryRepository;
        this.truckService = truckService;
        this.inventoryItemService = inventoryItemService;
        this.deliveryItemRepository = deliveryItemRepository;
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    public List<Delivery> getDeliveriesByOrderId(Integer orderId) {
        return deliveryRepository.findByOrderId(orderId);
    }

    public Delivery getDeliveryById(Long id) throws CustomException {
        Optional<Delivery> optionalDelivery = deliveryRepository.findById(id);
        return optionalDelivery.orElseThrow(() -> new CustomException(400,"Bad Request", Constants.DELIVERY_NOT_FOUND));
    }

    public void save(Delivery delivery, String status) throws CustomException {
        if (DeliveryStatus.valueOf(status) == DeliveryStatus.DELIVERED) {
            delivery.setStatus(DeliveryStatus.DELIVERED);
            deliveryRepository.save(delivery);
        }
    }

    public Delivery createDelivery(Delivery delivery) {
        return deliveryRepository.save(delivery);
    }

    public void scheduleDelivery(Delivery delivery) throws CustomException {
        if (delivery.getStartDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new CustomException(400, "Bad Request", Constants.NO_DELIVERY_ON_SUNDAY);
        }

        Order order = orderService.retrieveOrder(delivery.getOrderId());
        int totalItems = calculateTotalItems(order.getItems());
        checkAvailavility(delivery.getTruck());

        int freeSpace = calculateFreeSpace(delivery.getTruck());
        if (totalItems > freeSpace) {
            throw new CustomException(400, "Bad Request", Constants.NOT_ENOUGH_TRUCKS);
        }

        for (Truck truck : delivery.getTruck()) {
            if (totalItems > 0) {
                Truck deliveryTruck = truckService.getTruckByChassisNumber(truck.getChassisNumber());
                if(deliveryTruck.getTruckStatus() == TruckStatus.AVAILABLE){

                int itemsToDeliver = Math.min(totalItems, 10);
                List<DeliveryItem> deliveryItems = getDeliveryItems(order.getItems(), itemsToDeliver);
                subtractItems(order.getItems(), deliveryItems);
                Delivery newDelivery = new Delivery(deliveryItems, order.getOrder_number(), DeliveryStatus.UNDER_DELIVERY, delivery.getStartDate(), truck.getChassisNumber());

                newDelivery = deliveryRepository.save(newDelivery);

                for (DeliveryItem item : deliveryItems) {
                    item.setId(Math.abs(UUID.randomUUID().hashCode() & Integer.MAX_VALUE));
                    item.setDelivery(newDelivery);
                    deliveryItemRepository.save(item);
                }
                order.setStatus(Status.UNDER_DELIVERY);
                orderService.saveOrder(order, order.getUsername());
                updateInventory(deliveryItems);
                deliveryTruck.setTruckStatus(TruckStatus.UNDER_DELIVERY);
                truckService.updateTruck(truck.getChassisNumber(), deliveryTruck);
                totalItems -= itemsToDeliver;
            }}}
    }

    private int calculateTotalItems(List<Item> items) {
        return items.stream().mapToInt(Item::getQuantity).sum();
    }

    private List<DeliveryItem> getDeliveryItems(List<Item> items, int quantity) {
        return items.stream()
                .limit(quantity)
                .map(item -> new DeliveryItem(item.getItem(), Math.min(item.getQuantity(), quantity)))
                .toList();
    }

    private void updateInventory(List<DeliveryItem> deliveryItems) throws CustomException {
        for (DeliveryItem item : deliveryItems) {
            InventoryItem inventoryItem = inventoryItemService.getItemByName(item.getItem());
            if (inventoryItem.getQuantity() < item.getQuantity()) {
                throw new CustomException(400,"Bad Request",Constants.NOT_ENOUGH_PRODUCTS);
            }
            inventoryItem.setQuantity(inventoryItem.getQuantity() - item.getQuantity());
            inventoryItemService.saveOrUpdateItem(inventoryItem);
        }
    }

    private int calculateFreeSpace(List<Truck> trucks) {
        return trucks.size() * 10;
    }

    private void checkAvailavility(List<Truck> trucks) throws CustomException {
        for (Truck truck : trucks) {
            truckService.findByChassisNumberAndTruckStatus(truck);
        }
    }

    public static List<Item> generateItems(List<Item> originalItems, int totalQuantity) {
        List<Item> generatedItems = new ArrayList<>();
        int remainingQuantity = totalQuantity;

        for (Item item : originalItems) {
            if (remainingQuantity > 0) {
                int quantityToAdd = Math.min(remainingQuantity, item.getQuantity());
                generatedItems.add(new Item(item.getItem(), quantityToAdd));
                remainingQuantity -= quantityToAdd;
            } else {
                break;
            }
        }

        return generatedItems;
    }

    public static void subtractItems(List<Item> originalItems, List<DeliveryItem> itemsToRemove) {
        for (DeliveryItem itemToRemove : itemsToRemove) {
            for (Item originalItem : originalItems) {
                if (originalItem.getItem().equals(itemToRemove.getItem())) {
                    originalItem.setQuantity(originalItem.getQuantity() - itemToRemove.getQuantity());
                    break;
                }
            }
        }
    }

    @Async
    @Scheduled(cron = "0 0 4 * * *")
    public void checkDeliveries(){

        log.info("Checking delivery status");
        List<Delivery> deliveries = deliveryRepository.findByStatus(DeliveryStatus.UNDER_DELIVERY);
        LocalDate currentDate = LocalDate.now();

        for(Delivery delivery : deliveries){
            if (delivery.getStartDate().plusDays(1).isBefore(currentDate)){
                delivery.setStatus(DeliveryStatus.DELIVERED);
                deliveryRepository.save(delivery);
            }
        }
        log.info("Delivery status updated");

    }

}
