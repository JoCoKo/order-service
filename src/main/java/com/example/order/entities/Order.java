package com.example.order.entities;

import com.example.order.dto.OrderDTO;
import com.example.order.enums.OrderStatus;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int idOrder;
    @Column(name = "status")
    private OrderStatus status;
    @Column(name = "total_cost")
    private double totalCost;
    @Column(name = "total_amount")
    private int totalAmount;
    @Column(name = "user_name")
    private String userName;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems;

    public Order() {
        this.status = OrderStatus.COLLECTING;
    }

    public Order(double totalCost, int totalAmount, String userName, OrderItem... items) {
        this.status = OrderStatus.COLLECTING;
        this.totalCost = totalCost;
        this.totalAmount = totalAmount;
        this.userName = userName;
        this.orderItems = Stream.of(items).collect(Collectors.toSet());
        updateTotalAmount();
        updateTotalCost();
    }

    public Order(String userName, OrderItem... items) {
        this(0, 0, userName, items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public int getIdOrder() {
        return idOrder;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setIdOrder(int idOrder) {
        this.idOrder = idOrder;
    }

    public void updateTotalCost() {
        setTotalCost(
                orderItems
                        .stream()
                        .reduce(0.0,
                                (identity, item) -> identity + item.getPrice() * item.getAmount(),
                                Double::sum));
    }

    public void updateTotalAmount() {
        setTotalAmount(
                orderItems
                        .stream()
                        .reduce(0,
                                (identity, item) -> identity + item.getAmount(),
                                Integer::sum));
    }

    public Order addItem(OrderItem oi) {
        if (orderItems.contains(oi)){
            orderItems.stream().filter(data -> data.equals(oi)).findFirst().orElse(oi).add(oi.getAmount());
            updateTotalCost();
            updateTotalAmount();
        }
        else
            orderItems.add(oi);
        return this;
    }

    public OrderDTO toDTO() {
        return new OrderDTO(this);
    }

    @Override
    public String toString() {
        return "Order{" +
                "idOrder=" + idOrder +
                ", status=" + status +
                ", totalCost=" + totalCost +
                ", totalAmount=" + totalAmount +
                ", userName='" + userName + '\'' +
                ", orderItems=" + orderItems +
                '}';
    }
}
