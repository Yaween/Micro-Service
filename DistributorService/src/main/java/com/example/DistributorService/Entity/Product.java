package com.example.DistributorService.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false, unique = true, length = 50)
    private String productName;

    @Column(name = "product_description", nullable = false, length = 255)
    private String productDescription;

    @Column(name = "product_price")
    private double price;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DistributorProductMapper> distributorProductMappers;

}