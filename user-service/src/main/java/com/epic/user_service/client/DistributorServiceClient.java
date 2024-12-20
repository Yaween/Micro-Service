package com.epic.user_service.client;

import com.epic.user_service.dto.CommonResponse;
import com.epic.user_service.dto.admin.SendDistributorAddReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "distributor-service")
public interface DistributorServiceClient {

    @PostMapping("/api/distributor/add")
    ResponseEntity<CommonResponse> addDistributor(@RequestBody SendDistributorAddReq sendDistributorAddReq);
}
