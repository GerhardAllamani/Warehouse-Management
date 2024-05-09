package com.warehouse.management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "deliveryItem")
public class DeliveryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "item_name")
    private String item;

    @Column(name = "quantity")
    private int quantity;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    public DeliveryItem() {
    }

    public DeliveryItem(String item, int quantity, int unit_price) {
        this.item = item;
        this.quantity = quantity;
    }

    public DeliveryItem(String item, int quantity) {
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

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }
}
