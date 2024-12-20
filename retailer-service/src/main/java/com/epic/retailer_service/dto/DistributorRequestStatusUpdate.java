package com.epic.retailer_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistributorRequestStatusUpdate {
    private String username; //to validate JWT
    private String retailerReqId;
    private String option;
}
