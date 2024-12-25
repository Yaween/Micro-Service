package com.epic.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderReq {
    private String orderId;
    private String username; //For token validation
    private String option; //approve or reject after checking availability
}
