package com.example.DistributorService.Service;

import com.example.DistributorService.DTO.*;
import com.example.DistributorService.Entity.RetailerDistributorMapper;
import com.example.DistributorService.Entity.RetailerRequest;
import com.example.DistributorService.Repository.RetailerDistributorMapperRepository;
import com.example.DistributorService.Repository.RetailerRequestRepository;
import com.example.DistributorService.Util.JWTValidator;
import com.example.DistributorService.Util.RequestValidator;
import com.example.DistributorService.Util.UniqueIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class RetailerDistributorService {

    @Autowired
    private RetailerDistributorMapperRepository retailerDistributorMapperRepository;

    @Autowired
    private RetailerRequestRepository retailerRequestRepository;


    public ResponseEntity<CommonResponse> receiveRetailerRequest(String authorizationHeader, RetailerRequestDTO retailerRequestDTO) {
        CommonResponse response = new CommonResponse();

        // Validate Authorization Header
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("Authorization header is missing or invalid");
            response.setCode("INVALID_HEADER");
            response.setTitle("Invalid Authorization Header");
            response.setMessage("Authorization header is missing or invalid");
            return ResponseEntity.badRequest().body(response);
        }

        // Extract token from header
        String token = authorizationHeader.substring(7);

        // Validate the token
        JWTValidator jwtValidator = new JWTValidator();
        TokenData tokenData = jwtValidator.validateToken(token);

        if (tokenData == null) {
            log.warn("Token validation failed");
            response.setCode("INVALID_TOKEN");
            response.setTitle("Invalid Token");
            response.setMessage("The provided token is invalid or expired");
            return ResponseEntity.badRequest().body(response);
        }

        // Check if the user role is authorized
        if (!"distributor".equalsIgnoreCase(tokenData.getRole())) {
            log.warn("Unauthorized access attempt by user: {}", tokenData.getUsername());
            response.setCode("UNAUTHORIZED");
            response.setTitle("Unauthorized Access");
            response.setMessage("User does not have the required role to access this resource");
            return ResponseEntity.status(403).body(response);
        }

        // Validate distributorId matches token username
        if (!retailerRequestDTO.getDistributorId().equals(tokenData.getUsername())) {
            log.warn("Distributor ID in the request does not match token username");
            response.setCode("INVALID_DISTRIBUTOR");
            response.setTitle("Invalid Distributor ID");
            response.setMessage("Distributor ID in the request does not match the token username");
            return ResponseEntity.badRequest().body(response);
        }

        // Create and save the retailer request
        RetailerRequest retailerRequest = new RetailerRequest();
        retailerRequest.setRetailerRequestId(retailerRequestDTO.getRetailerRequestId());
        retailerRequest.setRetailerId(retailerRequestDTO.getRetailerId());
        retailerRequest.setDistributorId(retailerRequestDTO.getDistributorId());
        retailerRequest.setStatus("Pending");

        retailerRequestRepository.save(retailerRequest);

        log.info("Retailer request saved successfully for ID: {}", retailerRequestDTO.getRetailerRequestId());

        // Prepare the response
        response.setCode("SUCCESS");
        response.setTitle("Retailer Request Received");
        response.setMessage("Retailer request saved successfully");
        return ResponseEntity.ok(response);
    }


    // Method to get pending requests by distributorId

    public ResponseEntity<CommonResponse> getPendingRequests(String authorizationHeader, CheckRequest checkRequest) {
        CommonResponse response = new CommonResponse();

        // Validate Authorization Header
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("Authorization header is missing or invalid");
            response.setCode("INVALID_HEADER");
            response.setTitle("Invalid Authorization Header");
            response.setMessage("Authorization header is missing or invalid");
            return ResponseEntity.badRequest().body(response);
        }

        // Extract token from header
        String token = authorizationHeader.substring(7);

        // Validate the token
        JWTValidator jwtValidator = new JWTValidator();
        TokenData tokenData = jwtValidator.validateToken(token);

        if (tokenData == null) {
            log.warn("Token validation failed");
            response.setCode("INVALID_TOKEN");
            response.setTitle("Invalid Token");
            response.setMessage("The provided token is invalid or expired");
            return ResponseEntity.badRequest().body(response);
        }

        // Check if the user role is authorized
        if (!"distributor".equalsIgnoreCase(tokenData.getRole())) {
            log.warn("Unauthorized access attempt by user: {}", tokenData.getUsername());
            response.setCode("UNAUTHORIZED");
            response.setTitle("Unauthorized Access");
            response.setMessage("User does not have the required role to access this resource");
            return ResponseEntity.status(403).body(response);
        }

        // Validate distributorId matches token username
        String distributorId = checkRequest.getDistributorId();
        if (!distributorId.equals(tokenData.getUsername())) {
            log.warn("Distributor ID in the request does not match token username");
            response.setCode("INVALID_DISTRIBUTOR");
            response.setTitle("Invalid Distributor ID");
            response.setMessage("Distributor ID in the request does not match the token username");
            return ResponseEntity.badRequest().body(response);
        }

        // Fetch pending requests for the distributorId
        List<RetailerRequest> pendingRequests = retailerRequestRepository.findByDistributorIdAndStatus(distributorId, "Pending");

        // Prepare the response
        if (pendingRequests.isEmpty()) {
            response.setCode("NO_DATA");
            response.setTitle("No Pending Requests");
            response.setMessage("No pending requests found for distributor ID: " + distributorId);
        } else {
            response.setCode("SUCCESS");
            response.setTitle("Pending Requests");
            response.setMessage("Fetched pending requests successfully");
            response.setData(pendingRequests);
        }

        return ResponseEntity.ok(response);
    }


    // Method to update the retailer request status
    public CommonResponse updateRetailerRequestStatus(RetailerRequestStatusDTO requestDTO, String token) {
        CommonResponse response = new CommonResponse();

        // Validate the token and user role
        boolean isTokenValid = new RequestValidator().validateReq(requestDTO.getUsername(), "RETAILER", token);
        if (!isTokenValid) {
            response.setCode("ERROR");
            response.setTitle("Unauthorized");
            response.setMessage("Invalid token or role");
            return response;
        }

        // Retrieve the retailer request
        RetailerRequest existingReq = retailerRequestRepository
                .findByRetailerRequestId(requestDTO.getRetailerRequestId())
                .orElse(null);

        if (existingReq == null) {
            response.setCode("ERROR");
            response.setTitle("Request Not Found");
            response.setMessage("Retailer request not found");
            return response;
        }

        // Update the request status
        existingReq.setStatus(requestDTO.getStatus());
        retailerRequestRepository.save(existingReq);

        // If status is 'approve', add to the retailer distributor mapper table
        if ("approve".equalsIgnoreCase(requestDTO.getStatus())) {
            RetailerDistributorMapper retailerDistributorMapper = new RetailerDistributorMapper();
            retailerDistributorMapper.setId(existingReq.getRetailerId() + existingReq.getDistributorId());
            retailerDistributorMapper.setRetailerRequestId(existingReq.getRetailerRequestId());
            retailerDistributorMapper.setRetailerId(existingReq.getRetailerId());
            retailerDistributorMapper.setDistributorId(existingReq.getDistributorId());
            retailerDistributorMapperRepository.save(retailerDistributorMapper);
        }

        // Prepare the response
        response.setCode("SUCCESS");
        response.setTitle("Success");
        response.setMessage("Retailer request status updated successfully");

        return response;
    }


//    public ResponseEntity<CommonResponse> checkDistributorAvailability(DistributorCheckDTO distributorCheckDTO) {
//        CommonResponse response = new CommonResponse();
//        String checkingKey = distributorCheckDTO.getRetailerId() + distributorCheckDTO.getDistributorId();
//
//        if(retailerDistributorMapperRepository.findById(checkingKey).isPresent()){
//            log.info("Record found");
//
//            response.setCode("0000");
//            response.setTitle("Success");
//            response.setMessage("Record Found");
//            return ResponseEntity.ok(response);
//
//        } else {
//            log.info("Record not found");
//
//            response.setCode("Code");
//            response.setTitle("Failed");
//            response.setMessage("Record Not Found");
//            return ResponseEntity.badRequest().body(response);
//        }
//    }

    public CommonResponse checkDistributorAvailability(DistributorCheckDTO distributorCheckDTO) {
        CommonResponse response = new CommonResponse();

        // Generate the unique checking key
        String checkingKey = distributorCheckDTO.getRetailerId() + distributorCheckDTO.getDistributorId();

        // Check if the record exists
        if (retailerDistributorMapperRepository.findById(checkingKey).isPresent()) {
            log.info("Record found");

            response.setCode("200");
            response.setTitle("Success");
            response.setMessage("Record Found");
        } else {
            log.info("Record not found");

            response.setCode("400");
            response.setTitle("Failed");
            response.setMessage("Record Not Found");
        }

        return response;
    }

}





