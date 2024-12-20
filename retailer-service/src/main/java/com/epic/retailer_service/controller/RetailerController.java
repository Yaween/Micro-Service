package com.epic.retailer_service.controller;

import com.epic.retailer_service.dto.*;
import com.epic.retailer_service.service.RetailerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/retailer")
@RequiredArgsConstructor
@Slf4j
public class RetailerController {
    private final RetailerService retailerService;

    /**
     * // Retailer adding to the retailer table
     */
    @PostMapping("/add")
    public ResponseEntity<CommonResponse> addRetailer(@RequestBody AddRetailerReq addDistributorReq){
        return retailerService.addRetailer(addDistributorReq);
    }

    /**
     * // Update retailer details
     */
    @PostMapping("/update")
    public ResponseEntity<CommonResponse> updateRetailer(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody UpdateRetailerReq updateRetailerReq){
        return retailerService.updateRetailer(authorizationHeader, updateRetailerReq);
    }

    /**
     * // Get all available distributors
     */
    @GetMapping("/get-distributors")
    public ResponseEntity<CommonResponse> getDistributors(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody String username
    ){
        return retailerService.getAllDistributors(authorizationHeader, username);
    }

    /**
     * // Requesting a distributor
     */
    @PostMapping("/request-distributor")
    public ResponseEntity<CommonResponse> requestDistributor(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody DistributorRequest distributorRequest){
        return retailerService.requestDistributor(authorizationHeader,distributorRequest);
    }

    /**
     * // Checking status of request sent to the distributor
     */
    @PostMapping("/distributor-req-status-check")
    public ResponseEntity<CommonResponse> requestDistributorStatusCheck(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody DistributorRequestStatusCheck distributorRequestStatusCheck){
        return retailerService.requestDistributorStatusCheck(authorizationHeader, distributorRequestStatusCheck);
    }

    /**
     * // Updating the request status of Distributor request after distributors response
     */
    @PostMapping("/distributor-req-status-update")
    public ResponseEntity<CommonResponse> requestDistributorStatusUpdate(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody DistributorRequestStatusUpdate distributorRequestStatusUpdate){
        return retailerService.requestDistributorStatusUpdate(authorizationHeader, distributorRequestStatusUpdate);
    }

    /**
     * // Creating an order request
     */
    @PostMapping("/create-order-req")
    public ResponseEntity<CommonResponse> createOrderRequest(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody CreateOrderReq createOrderReq ){
        return retailerService.createOrderRequest(authorizationHeader, createOrderReq);
    }

    /**
     * // Check an order request status
     */
    @PostMapping("/check-order-req-status")
    public ResponseEntity<CommonResponse> checkOrderReqStatus(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody CheckOrderReqStatus checkOrderReqStatus ){
        return retailerService.checkOrderReqStatus(authorizationHeader, checkOrderReqStatus);
    }
}
