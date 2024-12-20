package com.epic.retailer_service.entity;

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
@Table(name = "ORDER_REQUEST")
public class OrderRequest {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "RETAILER_ID")
    private String retailerId;

    @Column(name = "DISTRIBUTOR_ID")
    private String distributorId;

    @Column(name = "PRODUCT_ID")
    private String productId;

    @Column(name = "PRODUCT_COUNT")
    private Integer productCount;

    @Column(name = "STATUS")
    private String status;
}
