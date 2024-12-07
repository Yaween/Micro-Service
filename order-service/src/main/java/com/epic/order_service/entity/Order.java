package com.epic.order_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ORDER_DETAILS")
public class Order {

    @Id
    @Column(name = "ORDER_ID")
    private String orderId;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PRODUCT_ID")
    private String productId;

    @Column(name = "PRODUCT_COUNT")
    private Integer productCount;

    @Column(name = "ORDER_STATUS")
    private String orderStatus;
}
