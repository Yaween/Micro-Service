package com.epic.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddOrderReq {
    private String username;
    private String productId;
    private String productCount;
}
