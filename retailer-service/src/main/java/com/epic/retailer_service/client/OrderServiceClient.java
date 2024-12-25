package com.epic.retailer_service.client;

import com.epic.retailer_service.dto.CommonResponse;
import com.epic.retailer_service.dto.SendOrderReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("order-service")
public interface OrderServiceClient {

    @PostMapping("/api/order/receive-order")
    ResponseEntity<CommonResponse> receiveOrderReq(@RequestBody SendOrderReq sendOrderReq);
}
