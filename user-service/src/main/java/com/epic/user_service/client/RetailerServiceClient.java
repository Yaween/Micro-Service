package com.epic.user_service.client;

import com.epic.user_service.dto.CommonResponse;
import com.epic.user_service.dto.admin.SendRetailerAddReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "retailer-service")
public interface RetailerServiceClient {

    @PostMapping("/api/retailer/add")
    ResponseEntity<CommonResponse> addRetailer(@RequestBody SendRetailerAddReq sendRetailerAddReq);
}
