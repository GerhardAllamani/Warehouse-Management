package com.warehouse.management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "item_name")
    private String item;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "unit_price")
    private int unit_price;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "order_number")
    private Order order;

    public Item() {
    }

    public Item(String item, int quantity, int unit_price) {
        this.item = item;
        this.quantity = quantity;
        this.unit_price = unit_price;
    }

    public Item(String item, int quantity) {
        this.item = item;
        this.quantity = quantity;
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

}
