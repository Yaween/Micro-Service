package com.example.DistributorService.Controller;

import com.example.DistributorService.DTO.*;
import com.example.DistributorService.Service.RetailerDistributorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/distributor")
public class RetailerDistributorController {

    @Autowired
    private RetailerDistributorService retailerDistributorService;

    @PostMapping("/receiveRequest")
    public ResponseEntity<CommonResponse> receiveRetailerRequest(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody RetailerRequestDTO retailerRequestDTO) {
        return retailerDistributorService.receiveRetailerRequest(authorizationHeader, retailerRequestDTO);
    }

    // Endpoint to get pending requests by distributorId from JSON input

    @PostMapping("/pendingRequests")
    public ResponseEntity<CommonResponse> getPendingRequests(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CheckRequest checkRequest) {
        return retailerDistributorService.getPendingRequests(authorizationHeader, checkRequest);
    }


    @PostMapping("/updateRetailerRequestStatus")
    public ResponseEntity<CommonResponse> updateRetailerRequestStatus(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody RetailerRequestStatusDTO requestDTO) {

        String token = authorizationHeader.substring(7); // Extract token from header
        CommonResponse response = retailerDistributorService.updateRetailerRequestStatus(requestDTO, token);

        return ResponseEntity.ok(response);
    }

//    @PostMapping("/retailer-distributor-availability")
//    public ResponseEntity<CommonResponse> checkDistributorAvailability(
//            @RequestBody DistributorCheckDTO distributorCheckDTO
//    )
//    {
//        return retailerDistributorService.checkDistributorAvailability(distributorCheckDTO);
//    }

    @PostMapping("/retailer-distributor-availability")
    public ResponseEntity<CommonResponse> checkDistributorAvailability(
            @RequestBody DistributorCheckDTO distributorCheckDTO
    ) {
        CommonResponse response = retailerDistributorService.checkDistributorAvailability(distributorCheckDTO);
        return ResponseEntity.status(Integer.parseInt(response.getCode())).body(response);
    }


}






