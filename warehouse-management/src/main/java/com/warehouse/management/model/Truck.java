package com.warehouse.management.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "trucks")
public class Truck {

    @Id
    @Column(name = "chassis_number", unique = true, nullable = false)
    private String chassisNumber;

    @Column(name = "license_plate", unique = true)
    private String licensePlate;

    @Column(name = "truck_status")
    private TruckStatus truckStatus;

    @ManyToMany(mappedBy = "truck")
    private List<Delivery> deliveries;


    public Truck() {
    }

    public Truck(String chassisNumber, String licensePlate) {
        this.chassisNumber = chassisNumber;
        this.licensePlate = licensePlate;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(List<Delivery> deliveries) {
        this.deliveries = deliveries;
    }


    public TruckStatus getTruckStatus() {
        return truckStatus;
    }

    public void setTruckStatus(TruckStatus truckStatus) {
        this.truckStatus = truckStatus;
    }
}
