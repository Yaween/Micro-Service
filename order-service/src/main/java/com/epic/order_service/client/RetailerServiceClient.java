package com.epic.order_service.client;

import com.epic.order_service.dto.CommonResponse;
import com.epic.order_service.dto.SendOrderReqUpdate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("retailer-service")
public interface RetailerServiceClient {

    @PostMapping("/api/retailer/update-order-req-status")
    ResponseEntity<CommonResponse> updateOrderReqStatus(@RequestBody SendOrderReqUpdate sendOrderReqUpdate);
}
