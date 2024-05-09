package com.warehouse.management.model;

public class OrderSummary {

    private Integer id;
    private String username;
    private Status status;

    public OrderSummary(Integer id, String username, Status status) {
        this.id = id;
        this.username = username;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
