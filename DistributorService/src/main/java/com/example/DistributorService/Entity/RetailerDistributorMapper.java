package com.example.DistributorService.Entity;

import com.example.DistributorService.Util.UniqueIdGenerator;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "retailer_distributor_mapper")
public class RetailerDistributorMapper {

    @Id
    @Column(name = "id")
    private String id; // Unique generated ID

    @Column(name = "retailer_request_id", nullable = false)
    private String retailerRequestId;

    @Column(name = "retailer_id", nullable = false)
    private String retailerId;

    @Column(name = "distributor_id", nullable = false)
    private String distributorId;


    public RetailerDistributorMapper() {
        this.id = UniqueIdGenerator.generateUniqueId(); // Auto-generate unique ID
    }

}
