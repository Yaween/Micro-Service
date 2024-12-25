package com.epic.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("distributor-service")
public interface DistributorServiceClient {

    @PostMapping("/api/distributor/retailer-request")
    boolean productAvailability(@RequestBody String productId);
}
