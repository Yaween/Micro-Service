package com.epic.order_service.client;

import com.epic.order_service.dto.ProductAvailabilityCheckDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("distributor-service")
public interface DistributorServiceClient {

    @PostMapping("/api/products/availabilityCheck")
    boolean productAvailability(@RequestBody ProductAvailabilityCheckDTO productAvailabilityCheckDTO);
}
