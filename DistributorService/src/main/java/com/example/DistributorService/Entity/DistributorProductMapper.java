package com.example.DistributorService.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "distributor_product_mapper")
public class DistributorProductMapper {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "distributor_id")
    private String distributorId;

    @Column(name = "product_quantity")
    private Integer productQuantity;

    @Column(name = "product_price")
    private double price;

}
