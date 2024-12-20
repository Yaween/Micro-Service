package com.epic.order_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "ORDER")
public class Order {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "RETAILER_ORDER_ID")
    private String retailerOrderId;

    @Column(name = "RETAILER_ID")
    private String retailerId;

    @Column(name = "_DISTRIBUTOR_ID")
    private String distributorId;

    @Column(name = "PRODUCT_ID")
    private String productId;

    @Column(name = "PRODUCT_COUNT")
    private Integer productCount;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATED_TIME")
    private LocalDateTime createdTime;

    @Column(name = "UPDATED_TIME")
    private LocalDateTime updatedTime;
}
