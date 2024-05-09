package com.warehouse.management.service;

import com.warehouse.management.model.Item;
import com.warehouse.management.repository.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(String id) {
        return itemRepository.findById(id);
    }

    public Item saveOrUpdateItem(Item item) {
        return itemRepository.save(item);
    }

    @Transactional
    public void deleteItem(Integer id) {
        itemRepository.deleteById(id);
    }
}
