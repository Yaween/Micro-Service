package com.epic.retailer_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendDistributorReq {
    private String retailerId;
    private String retailerUserId;
    private String distributorId;
    private String retailerReqId;
}
