package com.epic.retailer_service.client;

import com.epic.retailer_service.dto.CommonResponse;
import com.epic.retailer_service.dto.SendUserIdRetrieve;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("user-service")
public interface UserServiceClient {

    @PostMapping("/api/user/retrieveUserId")
    ResponseEntity<CommonResponse> retrieveUserId(@RequestBody SendUserIdRetrieve sendUserIdRetrieve);
}
