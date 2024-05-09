package com.warehouse.management.repository;

import com.warehouse.management.model.InventoryItem;
import com.warehouse.management.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, String> {


    void deleteById(Integer id);

    Optional<InventoryItem> findByItem(String itemName);


}
