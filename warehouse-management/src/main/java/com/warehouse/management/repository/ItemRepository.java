package com.warehouse.management.repository;

import com.warehouse.management.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

    Item findByItem(String itemName);

    void deleteById(Integer id);

}
