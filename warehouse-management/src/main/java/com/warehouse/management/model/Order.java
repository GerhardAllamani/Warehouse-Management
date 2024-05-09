package com.warehouse.management.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "order_number",
        "submitted_date",
        "deadline_date",
        "status",
        "reason",
        "items",
        "user"
})
@Entity
@Table(name = "orders")
public class Order {

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    @Transient
    private String username;

    @Id
    @Column(name = "order_number")
    private Integer order_number;

    @Nullable
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "submitted_date")
    private LocalDate submitted_date;

    @Nullable
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "deadline_date")
    private LocalDate deadline_date;

    @Column(name = "status")
    private Status status;

    @Nullable
    @Column(name = "reason")
    private String reason;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private List<Item> items;

    public LocalDate getDeadline_date() {
        return deadline_date;
    }

    public void setDeadline_date(LocalDate deadline_date) {
        this.deadline_date = deadline_date;
    }

    public LocalDate getSubmitted_date() {
        return submitted_date;
    }

    public void setSubmitted_date(LocalDate submitted_date) {
        this.submitted_date = submitted_date;
    }

    public Integer getOrder_number() {
        return order_number;
    }

    public void setOrder_number(Integer order_number) {
        this.order_number = order_number;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return user != null ? user.getUsername() : username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    public String getReason() {
        return reason;
    }

    public void setReason(@Nullable String reason) {
        this.reason = reason;
    }
}
