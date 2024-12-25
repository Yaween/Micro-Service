package com.epic.retailer_service.client;

import com.epic.retailer_service.dto.CommonResponse;
import com.epic.retailer_service.dto.SendDistributorReq;
import com.epic.retailer_service.dto.SendOrderReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("distributor-service")
public interface DistributorServiceClient {

    @PostMapping("/api/distributor/retailer-request")
    ResponseEntity<CommonResponse> requestDistributor(@RequestBody SendDistributorReq sendDistributorReq);

    @GetMapping("/api/distributor/getAllDistributors")
    ResponseEntity<CommonResponse> getAllDistributors();

    @GetMapping("/api/distributor/getAllProducts")
    ResponseEntity<CommonResponse> getAllProducts();
}
