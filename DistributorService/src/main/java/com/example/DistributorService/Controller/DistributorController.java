package com.example.DistributorService.Controller;

import com.example.DistributorService.DTO.AddDistributorReq;
import com.example.DistributorService.DTO.CommonResponse;
import com.example.DistributorService.DTO.UpdateBusinessNameRequest;
import com.example.DistributorService.Entity.Distributor;
import com.example.DistributorService.Service.DistributorService;
import com.example.DistributorService.DTO.DistributorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/distributor")
public class DistributorController {

    @Autowired
    private DistributorService distributorService;

    @GetMapping("/getAllDistributors")
    public ResponseEntity<CommonResponse> getAllDistributors() {
//        CommonResponse response = distributorService.getAllDistributors();
//        return ResponseEntity.status(Integer.parseInt(response.getCode())).body(response);
        return distributorService.getAllDistributors();
    }

    // Endpoint to add distributor
    @PostMapping("/add")
    public ResponseEntity<CommonResponse> addDistributor(@RequestBody AddDistributorReq addDistributorReq) {
        return distributorService.addDistributor(addDistributorReq);
    }

    @PostMapping("/update-distributor")
    public ResponseEntity<CommonResponse> updateDistributor(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody UpdateBusinessNameRequest updateBusinessNameRequest
            ){
        return distributorService.updateDistributor(authorizationHeader, updateBusinessNameRequest);
    }
}


