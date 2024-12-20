package com.epic.retailer_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendOrderReq {
    private String orderId;
    private String retailerId;
    private String distributorId;
    private String productId;
    private Integer productCount;
}
