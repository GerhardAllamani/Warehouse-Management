package com.warehouse.management.controller;

import com.warehouse.management.exception.CustomException;
import com.warehouse.management.model.InventoryItem;
import com.warehouse.management.service.InventoryItemService;
import com.warehouse.management.service.ItemService;
import com.warehouse.management.Constants;
import com.warehouse.management.model.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/item")
public class ItemController {

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    private final InventoryItemService inventoryItemService;

    @Autowired
    public ItemController(InventoryItemService inventoryItemService, ItemService itemService) {
        this.inventoryItemService = inventoryItemService;
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems() {
        logger.info("Fetching all items");
        try {
            List<InventoryItem> items = inventoryItemService.getAllItems();
            logger.info("Items fetched successfully");
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            Error error = new Error();
            error.setMessage(Constants.ERROR);
            error.setCode(500);
            logger.error("Error occurred while fetching items: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable String id) {
        logger.info("Fetching item by ID: {}", id);
        try {
            Optional<InventoryItem> item = inventoryItemService.getItemById(id);
            if (item.isPresent()) {
                logger.info("Item fetched successfully: {}", id);
                return ResponseEntity.ok(item.get());
            } else {
                logger.warn("Item not found with ID: {}", id);
                return ResponseEntity.ok().body("[]");
            }
        } catch (Exception e) {
            Error error = new Error();
            error.setMessage(Constants.ERROR);
            error.setCode(500);
            logger.error("Error occurred while fetching item {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody InventoryItem item) {
        logger.info("Creating a new item");
        try {
            if(inventoryItemService.getItemByName(item.getItem()) != null){
                throw new CustomException(400, "Bad Request", Constants.ITEM_ALREADY_EXISTS);
            }
            InventoryItem savedItem = inventoryItemService.saveOrUpdateItem(item);
            logger.info("Item created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
        }catch (CustomException e) {
            Error error = e.getError();
            logger.error(e.getMessage(), error.getReason(), e.getMessage());
            return ResponseEntity.status(e.getError().getCode()).body(error);
        }
        catch (Exception e) {
            Error error = new Error();
            error.setMessage(Constants.ERROR);
            error.setCode(500);
            logger.error("Error occurred while creating item: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@PathVariable Integer id, @RequestBody InventoryItem item) {
        logger.info("Updating item with ID: {}", id);
        try {
            item.setId(id);
            InventoryItem updatedItem = inventoryItemService.saveOrUpdateItem(item);
            logger.info("Item updated successfully: {}", id);
            return ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            Error error = new Error();
            error.setMessage(Constants.ERROR);
            error.setCode(500);
            logger.error("Error occurred while updating item {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable Integer id) {
        logger.info("Deleting item with ID: {}", id);
        try {
            inventoryItemService.deleteItem(id);
            logger.info("Item deleted successfully: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            Error error = new Error();
            error.setMessage(Constants.ERROR);
            error.setCode(500);
            logger.error("Error occurred while deleting item {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
