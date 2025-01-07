package com.example.DistributorService.client;

import com.example.DistributorService.DTO.CommonResponse;
import com.example.DistributorService.DTO.SendUserIdRetrieve;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("user-service")
public interface UserServiceClient {

    @PostMapping("/api/user/retrieveUserId")
    ResponseEntity<CommonResponse> retrieveUserId(@RequestBody SendUserIdRetrieve sendUserIdRetrieve);
}
