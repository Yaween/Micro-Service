package com.example.DistributorService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendRetailerReqStatusUpdate {
    private String username; //to validate JWT
    private String retailerReqId;
    private String option;
}
