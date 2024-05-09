package com.warehouse.management.service;

import com.warehouse.management.Constants;
import com.warehouse.management.exception.CustomException;
import com.warehouse.management.model.InventoryItem;
import com.warehouse.management.model.Item;
import com.warehouse.management.repository.InventoryItemRepository;
import com.warehouse.management.repository.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;

    @Autowired
    public InventoryItemService(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    public List<InventoryItem> getAllItems() {
        return inventoryItemRepository.findAll();
    }

    public Optional<InventoryItem> getItemById(String id) {
        return inventoryItemRepository.findById(id);
    }

    public InventoryItem saveOrUpdateItem(InventoryItem item) {
        return inventoryItemRepository.save(item);
    }

    public InventoryItem getItemByName(String itemName) {
        Optional<InventoryItem> optionalItem =  inventoryItemRepository.findByItem(itemName);
        return  optionalItem.orElse(null);

    }

    public void checkInventory(List<Item> items) throws CustomException {
        for(Item item : items) {
        Optional<InventoryItem> optionalItem =  inventoryItemRepository.findByItem(item.getItem());
            if (optionalItem.isPresent()) {
                if(item.getQuantity() > optionalItem.get().getQuantity()) {
                    throw new CustomException(400, "Bad Request", Constants.NOT_ENOUGH_PRODUCTS);
                }
            }
            else{
                throw new CustomException(400, "Bad Request", Constants.PRODUCT_NOT_FOUND);
            }
        }
    }

    @Transactional
    public void deleteItem(Integer id) {
        inventoryItemRepository.deleteById(id);
    }
}
