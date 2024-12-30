package com.example.DistributorService.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "DISTRIBUTOR")
public class Distributor {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "BUSINESS_NAME")
    private String businessName;

    @Column(name = "LOGIN_STATUS")
    private String status;

}
