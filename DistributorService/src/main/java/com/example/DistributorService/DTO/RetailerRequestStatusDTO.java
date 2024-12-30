package com.example.DistributorService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetailerRequestStatusDTO {
    private String username;
    private String retailerRequestId;
    private String status; // "approve" or "reject"
}
