package com.warehouse.management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "item_number")
    private int itemNumber;

    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "status")
    private DeliveryStatus status;

    @Column(name = "chassis_number")
    private String chassisNumber;

    @OneToMany(mappedBy = "delivery", fetch = FetchType.EAGER)
    private List<DeliveryItem> items;

    @ManyToMany
    @JoinTable(
            name = "delivery_truck",
            joinColumns = @JoinColumn(name = "delivery_id"),
            inverseJoinColumns = @JoinColumn(name = "chassis_number")
    )
    private List<Truck> truck;

    public Delivery(List<DeliveryItem> deliveryItems, Integer orderNumber, DeliveryStatus deliveryStatus, LocalDate startDate, String chassisNumber) {
        this.orderId = orderNumber;
        this.status = deliveryStatus;
        this.startDate = startDate;
        this.chassisNumber = chassisNumber;
        this.items = deliveryItems;
    }

    public Delivery() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }


    public int getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(int itemNumber) {
        this.itemNumber = itemNumber;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public List<Truck> getTruck() {
        return truck;
    }

    public void setTruck(List<Truck> truck) {
        this.truck = truck;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public List<DeliveryItem> getItems() {
        return items;
    }

    public void setItems(List<DeliveryItem> items) {
        this.items = items;
    }
}
