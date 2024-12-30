package com.example.DistributorService.Entity;

import com.example.DistributorService.DTO.RetailerRequestDTO;
import com.example.DistributorService.Util.UniqueIdGenerator;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "retailer_request")
public class RetailerRequest extends RetailerRequestDTO {

    @Id
    @Column(name = "id", length = 16)
    private String id; // Unique generated ID

    @Column(name = "retailer_request_id", nullable = false)
    private String retailerRequestId;

    @Column(name = "retailer_id", nullable = false)
    private String retailerId;

    @Column(name = "distributor_id", nullable = false)
    private String distributorId;

    @Column(name = "status", length = 20)
    private String status;

    public RetailerRequest() {
        this.id = UniqueIdGenerator.generateUniqueId(); // Auto-generate unique ID
    }

}
