package com.example.DistributorService.client;

import com.example.DistributorService.DTO.CommonResponse;
import com.example.DistributorService.DTO.SendRetailerReqStatusUpdate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("retailer-service")
public interface RetailerServiceClient {

    @PostMapping("/api/retailer/distributor-req-status-update")
    ResponseEntity<CommonResponse> requestDistributorStatusUpdate(SendRetailerReqStatusUpdate sendRetailerReqStatusUpdate);
}
