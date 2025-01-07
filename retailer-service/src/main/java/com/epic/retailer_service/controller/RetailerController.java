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
        log.info("Add Retailer Request Received");
        return retailerService.addRetailer(addDistributorReq);
    }

    /**
     * // Update retailer details
     */
    @PostMapping("/update")
    public ResponseEntity<CommonResponse> updateRetailer(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody UpdateRetailerReq updateRetailerReq){
        log.info("Update Retailer Request Received");
        return retailerService.updateRetailer(authorizationHeader, updateRetailerReq);
    }

    /**
     * // Get all available distributors
     */
    @PostMapping("/get-distributors")
    public ResponseEntity<CommonResponse> getDistributors(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody GetAllDistributorsReq getAllDistributorsReq
    ){
        log.info("Get Distributors Request Received");
        return retailerService.getAllDistributors(authorizationHeader, getAllDistributorsReq);
    }

    /**
     * // Get all available products
     */
    @PostMapping("/get-products")
    public ResponseEntity<CommonResponse> getProducts(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody GetAllProductsReq getAllProductsReq
    ){
        log.info("Get Products Request Received");
        return retailerService.getProducts(authorizationHeader, getAllProductsReq);
    }

    /**
     * // Requesting a distributor
     */
    @PostMapping("/request-distributor")
    public ResponseEntity<CommonResponse> requestDistributor(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody DistributorRequest distributorRequest){
        log.info("Request Distributor Request Received");
        return retailerService.requestDistributor(authorizationHeader,distributorRequest);
    }

    /**
     * // Checking status of request sent to the distributor
     */
    @PostMapping("/distributor-req-status-check")
    public ResponseEntity<CommonResponse> requestDistributorStatusCheck(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody DistributorRequestStatusCheck distributorRequestStatusCheck){
        log.info("Request Distributor Status Check Request Received");
        return retailerService.requestDistributorStatusCheck(authorizationHeader, distributorRequestStatusCheck);
    }

    /**
     * // Updating the request status of Distributor request after distributors response
     */
    @PostMapping("/distributor-req-status-update")
    public ResponseEntity<CommonResponse> requestDistributorStatusUpdate(
            @RequestBody DistributorRequestStatusUpdate distributorRequestStatusUpdate){
        log.info("Request Distributor Status Update Request Received");
        return retailerService.requestDistributorStatusUpdate(distributorRequestStatusUpdate);
    }

    /**
     * // Creating an order request
     */
    @PostMapping("/create-order-req")
    public ResponseEntity<CommonResponse> createOrderRequest(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody CreateOrderReq createOrderReq ){
        log.info("Create Order Request Received");
        return retailerService.createOrderRequest(authorizationHeader, createOrderReq);
    }

    /**
     * // Check an order request status
     */
    @PostMapping("/check-order-req-status")
    public ResponseEntity<CommonResponse> checkOrderReqStatus(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody CheckOrderReqStatus checkOrderReqStatus ){
        log.info("Check Order Req Status Request Received");
        return retailerService.checkOrderReqStatus(authorizationHeader, checkOrderReqStatus);
    }

    /**
     * // Receiving an order request status update
     */
    @PostMapping("/update-order-req-status")
    public ResponseEntity<CommonResponse> updateOrderReqStatus(
            @RequestBody UpdateOrderReqStatus updateOrderReqStatus){
        log.info("Update Order Req Status Request Received");
        return retailerService.updateOrderReqStatus(updateOrderReqStatus);
    }
}
