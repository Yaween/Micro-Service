package com.epic.retailer_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRequestOrder {
    private String username;
    private String distributorId;
    private String productId;
    private String productCount;
}
