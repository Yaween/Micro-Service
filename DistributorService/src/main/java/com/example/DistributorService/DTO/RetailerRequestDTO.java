package com.example.DistributorService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetailerRequestDTO {
    private String username;
    private String retailerRequestId;
    private String retailerId;
    private String distributorId;

}
