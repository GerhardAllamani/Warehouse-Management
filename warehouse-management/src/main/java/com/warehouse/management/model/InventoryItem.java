package com.warehouse.management.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inventoryItem")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "item_name", unique = true)
    private String item;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "unit_price")
    private int unit_price;

    public InventoryItem() {
    }

    public InventoryItem(String item, int quantity, int unit_price) {
        this.item = item;
        this.quantity = quantity;
        this.unit_price = unit_price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(int unit_price) {
        this.unit_price = unit_price;
    }

}
