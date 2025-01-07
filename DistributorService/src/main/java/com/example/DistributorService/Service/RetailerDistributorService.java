package com.example.DistributorService.Service;

import com.example.DistributorService.Configuration.InitConfig;
import com.example.DistributorService.DTO.*;
import com.example.DistributorService.Entity.Distributor;
import com.example.DistributorService.Entity.RetailerDistributorMapper;
import com.example.DistributorService.Entity.RetailerRequest;
import com.example.DistributorService.Repository.DistributorRepository;
import com.example.DistributorService.Repository.RetailerDistributorMapperRepository;
import com.example.DistributorService.Repository.RetailerRequestRepository;
import com.example.DistributorService.Util.JWTValidator;
import com.example.DistributorService.Util.RequestValidator;
import com.example.DistributorService.Util.UniqueIdGenerator;
import com.example.DistributorService.client.RetailerServiceClient;
import com.example.DistributorService.client.UserServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RetailerDistributorService {

    @Autowired
    private RetailerDistributorMapperRepository retailerDistributorMapperRepository;

    @Autowired
    private RetailerRequestRepository retailerRequestRepository;

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private RetailerServiceClient retailerServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;


    public ResponseEntity<CommonResponse> receiveRetailerRequest(RetailerRequestDTO retailerRequestDTO) {
        CommonResponse response = new CommonResponse();

        // Create and save the retailer request
        RetailerRequest retailerRequest = new RetailerRequest();
        retailerRequest.setRetailerRequestId(retailerRequestDTO.getRetailerReqId());
        retailerRequest.setRetailerId(retailerRequestDTO.getRetailerId());
        retailerRequest.setDistributorId(retailerRequestDTO.getDistributorId());
        retailerRequest.setStatus("Pending");

        retailerRequestRepository.save(retailerRequest);

        log.info("Retailer request saved successfully for ID: {}", retailerRequestDTO.getRetailerReqId());

        // Prepare the response
        response.setCode(InitConfig.SUCCESS);
        response.setTitle(InitConfig.TITLE_SUCCESS);
        response.setMessage("Retailer request saved successfully");
        return ResponseEntity.ok(response);
    }


    // Method to get pending requests by distributorId

    public ResponseEntity<CommonResponse> getPendingRequests(String authorizationHeader, CheckRequest checkRequest) {
        CommonResponse response = new CommonResponse();

        // Validate Authorization Header
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("Authorization header is missing or invalid");
            response.setCode(InitConfig.TOKEN_MISSING);
            response.setTitle(InitConfig.TITLE_UNAUTHORIZED);
            response.setMessage("Authorization header is missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Extract token from header
        String token = authorizationHeader.substring(7);

        // Validate the token
        JWTValidator jwtValidator = new JWTValidator();
        TokenData tokenData = jwtValidator.validateToken(token);

        if (tokenData == null) {
            log.warn("Token validation failed");
            response.setCode(InitConfig.TOKEN_INVALID_EXPIRED);
            response.setTitle(InitConfig.TITLE_UNAUTHORIZED);
            response.setMessage("The provided token is invalid or expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Check if the user role is authorized
        if (!"distributor".equalsIgnoreCase(tokenData.getRole())) {
            log.warn("Unauthorized access attempt by user: {}", tokenData.getUsername());
            response.setCode(InitConfig.UNAUTHORIZED_ACCESS);
            response.setTitle(InitConfig.TITLE_UNAUTHORIZED);
            response.setMessage("User does not have the required role to access this resource");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        SendUserIdRetrieve sendUserIdRetrieve = new SendUserIdRetrieve(checkRequest.getUsername());
        String userId = userServiceClient.retrieveUserId(sendUserIdRetrieve).getBody().getUserData().getUserId();
        log.info("Retrieved user ID: {}", userId);

        Distributor existingDistributor = distributorRepository.findByUserId(userId).
                orElseThrow(null);

        String distributorId = existingDistributor.getId();

        List<RetailerRequest> retailerRequestList = retailerRequestRepository.findByDistributorId(distributorId);

        if(retailerRequestList.isEmpty()){
            log.info("Retailer requests list is empty");

            response.setCode(InitConfig.LIST_EMPTY);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("Retailer requests list is empty");
            return ResponseEntity.badRequest().body(response);

        } else {
            log.info("List is not empty");

            List<Map<String, Object>> filteredReqList = new ArrayList<>();
            for (RetailerRequest retailerRequest : retailerRequestList) {
                Map<String, Object> map = new HashMap<>();
                if(retailerRequest.getStatus().equalsIgnoreCase("PENDING")){
                    map.put("id", retailerRequest.getId());
                    map.put("retailerId", retailerRequest.getRetailerId());
                    map.put("requestId", retailerRequest.getRetailerRequestId());
                    filteredReqList.add(map);
                }
            }

            if(filteredReqList.isEmpty()){
                response.setCode(InitConfig.SUCCESS);
                response.setTitle(InitConfig.TITLE_SUCCESS);
                response.setMessage("No Any Pending Requests");
                return ResponseEntity.ok(response);

            } else {
                response.setCode(InitConfig.SUCCESS);
                response.setTitle(InitConfig.TITLE_SUCCESS);
                response.setMessage("Retailer requests list retrieved successfully");
                response.setRetailerRequestPendingList(filteredReqList);
                return ResponseEntity.ok(response);
            }
        }
    }


    // Method to update the retailer request status
    public ResponseEntity<CommonResponse> updateRetailerRequestStatus(RetailerRequestStatusDTO requestDTO, String token) {
        CommonResponse response = new CommonResponse();

        // Validate the token and user role
        boolean isTokenValid = new RequestValidator().validateReq(requestDTO.getUsername(), "DISTRIBUTOR", token);
        if (!isTokenValid) {
            response.setCode(InitConfig.TOKEN_INVALID_EXPIRED);
            response.setTitle(InitConfig.TITLE_UNAUTHORIZED);
            response.setMessage("Invalid token or role");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Retrieve the retailer request
        RetailerRequest existingReq = retailerRequestRepository
                .findByRetailerRequestId(requestDTO.getRetailerRequestId())
                .orElse(null);

        if (existingReq == null) {
            response.setCode(InitConfig.REQUEST_NOT_FOUND);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("Retailer request not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        SendUserIdRetrieve sendUserIdRetrieve = new SendUserIdRetrieve(requestDTO.getUsername());
        String userId = userServiceClient.retrieveUserId(sendUserIdRetrieve).getBody().getUserData().getUserId();

        if(userId == null || userId.isEmpty()){
            log.info("Username is invalid");

            response.setCode(InitConfig.USERNAME_NOT_FOUND);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("Username is invalid");
            return ResponseEntity.badRequest().body(response);
        }

        Distributor distributor = distributorRepository.findByUserId(userId)
                .orElseThrow(null);

        String distributorId = distributor.getId();
        String distributorIdNew = existingReq.getDistributorId();

        if(!distributorId.equals(distributorIdNew)){
            log.info("The distributor id is different");

            response.setCode(InitConfig.UNAUTHORIZED_ACCESS);
            response.setTitle(InitConfig.TITLE_UNAUTHORIZED);
            response.setMessage("The distributor has no permission to change that request");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Update the request status
        if(requestDTO.getStatus().equalsIgnoreCase("APPROVE")){
            existingReq.setStatus("APPROVED");
            retailerRequestRepository.save(existingReq);

            RetailerDistributorMapper retailerDistributorMapper = new RetailerDistributorMapper();
            retailerDistributorMapper.setId(existingReq.getRetailerId() + existingReq.getDistributorId());
            retailerDistributorMapper.setRetailerRequestId(existingReq.getRetailerRequestId());
            retailerDistributorMapper.setRetailerId(existingReq.getRetailerId());
            retailerDistributorMapper.setDistributorId(existingReq.getDistributorId());
            retailerDistributorMapperRepository.save(retailerDistributorMapper);

            SendRetailerReqStatusUpdate sendRetailerReqStatusUpdate = new SendRetailerReqStatusUpdate();
            sendRetailerReqStatusUpdate.setRetailerReqId(existingReq.getRetailerRequestId());
            sendRetailerReqStatusUpdate.setUsername(requestDTO.getUsername());
            sendRetailerReqStatusUpdate.setOption("APPROVE");

            String code = retailerServiceClient.requestDistributorStatusUpdate(sendRetailerReqStatusUpdate).getBody().getCode();

            if (code == null || code.isEmpty()) {
                log.info("response is not successful");

                response.setCode(InitConfig.UNSUCCESSFUL_RESPONSE);
                response.setTitle(InitConfig.TITLE_FAILED);
                response.setMessage("Response is not successful");
                return ResponseEntity.badRequest().body(response);
            }

            if(code.equalsIgnoreCase(InitConfig.SUCCESS)){

                response.setCode(InitConfig.SUCCESS);
                response.setTitle(InitConfig.TITLE_SUCCESS);
                response.setMessage("Retailer request updated and informed successfully");

            } else {
                response.setCode(code);
                response.setTitle(InitConfig.TITLE_FAILED);
                response.setMessage("Retailer request status update failed");
            }
            return ResponseEntity.ok().body(response);

        } else if (requestDTO.getStatus().equalsIgnoreCase("REJECT")) {
            existingReq.setStatus("REJECTED");
            retailerRequestRepository.save(existingReq);

            SendRetailerReqStatusUpdate sendRetailerReqStatusUpdate = new SendRetailerReqStatusUpdate();
            sendRetailerReqStatusUpdate.setRetailerReqId(existingReq.getRetailerRequestId());
            sendRetailerReqStatusUpdate.setUsername(requestDTO.getUsername());
            sendRetailerReqStatusUpdate.setOption("REJECT");

            String code = retailerServiceClient.requestDistributorStatusUpdate(sendRetailerReqStatusUpdate).getBody().getCode();

            if (code == null || code.isEmpty()) {
                log.info("response is not successful");

                response.setCode(InitConfig.UNSUCCESSFUL_RESPONSE);
                response.setTitle(InitConfig.TITLE_FAILED);
                response.setMessage("Response is not successful");
                return ResponseEntity.badRequest().body(response);
            }

            if(code.equalsIgnoreCase(InitConfig.SUCCESS)){

                response.setCode(InitConfig.SUCCESS);
                response.setTitle(InitConfig.TITLE_SUCCESS);
                response.setMessage("Retail request updated and informed successfully");

            } else {
                response.setCode(code);
                response.setTitle(InitConfig.TITLE_FAILED);
                response.setMessage("Retailer request status update failed");
            }
            return ResponseEntity.ok().body(response);

        } else {
            log.info("Invalid Command");

            response.setCode(InitConfig.INVALID_COMMAND);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("Invalid Command. Try Approve/Reject");
            return ResponseEntity.badRequest().body(response);
        }
    }

    public ResponseEntity<CommonResponse> checkDistributorAvailability(DistributorCheckDTO distributorCheckDTO) {
        CommonResponse response = new CommonResponse();

        // Generate the unique checking key
        String checkingKey = distributorCheckDTO.getRetailerId() + distributorCheckDTO.getDistributorId();

        // Check if the record exists
        if (retailerDistributorMapperRepository.findById(checkingKey).isPresent()) {
            log.info("Record found");

            response.setCode(InitConfig.SUCCESS);
            response.setTitle(InitConfig.TITLE_SUCCESS);
            response.setMessage("Record Found");
        } else {
            log.info("Record not found");

            response.setCode(InitConfig.REQUEST_NOT_FOUND);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("Record Not Found");
        }

        return ResponseEntity.ok(response);
    }

}





