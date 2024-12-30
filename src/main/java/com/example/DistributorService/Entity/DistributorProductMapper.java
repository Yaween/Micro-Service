package com.example.DistributorService.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "distributor_product_mapper")
public class DistributorProductMapper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "distributor_id", nullable = false)
    private Long distributorId;

    @Column(name = "product_quantity", nullable = false)
    private Integer productQuantity;

    @Column(name="option", nullable = false)
    private String option;

}
