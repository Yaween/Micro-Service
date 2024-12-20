package com.epic.retailer_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "DISTRIBUTOR_REQUEST")
public class AddDistributorReq {
    @Id
    @Column(name = "ID", unique = true)
    private String id;

    @Column(name = "RETAILER_ID")
    private String retailerId;

    @Column(name = "DISTRIBUTOR_ID")
    private String distributorId;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "REQUEST_STATUS")
    private String requestStatus;

    @Column(name = "CREATED_TIME")
    private LocalDateTime createdTime;

    @Column(name = "UPDATED_TIME")
    private LocalDateTime updatedTime;
}
