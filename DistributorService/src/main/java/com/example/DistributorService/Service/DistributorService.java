package com.example.DistributorService.Service;


import com.example.DistributorService.Configuration.InitConfig;
import com.example.DistributorService.DTO.*;
import com.example.DistributorService.Entity.Distributor;
import com.example.DistributorService.Repository.DistributorRepository;
import com.example.DistributorService.Util.RequestValidator;
import com.example.DistributorService.Util.UniqueIdGenerator;
import com.example.DistributorService.client.UserServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DistributorService {

    @Autowired
    private DistributorRepository distributorRepository;
    @Autowired
    private UserServiceClient userServiceClient;

    public ResponseEntity<CommonResponse> addDistributor(AddDistributorReq addDistributorReq) {
        CommonResponse addDistributorResponse = new CommonResponse();

        Distributor distributor = new Distributor();
        String distributorId = UniqueIdGenerator.generateDistributorId();

        distributor.setId(distributorId);
        distributor.setUserId(addDistributorReq.getUserId());
        distributor.setStatus("INACTIVE");
        distributorRepository.save(distributor);

        addDistributorResponse.setCode(InitConfig.SUCCESS);
        addDistributorResponse.setTitle(InitConfig.TITLE_SUCCESS);
        addDistributorResponse.setMessage("Distributor added to the table successfully");
        return ResponseEntity.ok(addDistributorResponse);
    }


    public ResponseEntity<CommonResponse> getAllDistributors(){
        CommonResponse response = new CommonResponse();
        List<Distributor> distributors = distributorRepository.findAll();

        if (distributors != null && !distributors.isEmpty()) {
            // Map the entities to DTOs for the response
            List<DistributorData> distributorDataList = distributors.stream()
                    .map(distributor -> new DistributorData(
                            distributor.getId(),
                            distributor.getBusinessName(),
                            distributor.getUserId()))
                    .collect(Collectors.toList());

            // Set response details
            response.setCode(InitConfig.SUCCESS);
            response.setTitle(InitConfig.TITLE_SUCCESS);
            response.setMessage("Distributors retrieved successfully.");
            response.setDistributorList(distributorDataList);

        } else {
            // Handle the case when no distributors are found
            response.setCode(InitConfig.DISTRIBUTOR_NOT_FOUND);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("No distributors found.");
            response.setData(null);
        }
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<CommonResponse> updateDistributor(String authorizationHeader, UpdateBusinessNameRequest updateBusinessNameRequest) {
        CommonResponse response = new CommonResponse();
        RequestValidator requestValidator = new RequestValidator();

        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.warn("Authorization header is missing or empty. Request cannot be processed.");

            response.setCode(InitConfig.TOKEN_MISSING);
            response.setTitle(InitConfig.TITLE_UNAUTHORIZED);
            response.setMessage("Token is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Extract and validate the token
        String token = authorizationHeader.substring(7);
        boolean tokenValidity = requestValidator.validateReq(updateBusinessNameRequest.getUsername(), "DISTRIBUTOR", token);

        if(!tokenValidity){
            log.info("Token is invalid or expired");

            response.setCode(InitConfig.TOKEN_INVALID_EXPIRED);
            response.setTitle(InitConfig.TITLE_UNAUTHORIZED);
            response.setMessage("Token is Invalid or Expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        SendUserIdRetrieve sendUserIdRetrieve = new SendUserIdRetrieve(updateBusinessNameRequest.getUsername());

        //retrieving the user id from user service
        String userId = userServiceClient.retrieveUserId(sendUserIdRetrieve).getBody().getUserData().getUserId();

        if(userId == null || userId.isEmpty()){
            log.info("Invalid username");

            response.setCode(InitConfig.USERNAME_INVALID);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("Invalid Username");
        }

        //updating the business name
        Distributor distributor = distributorRepository.findByUserId(userId).
                orElseThrow(null);
        distributor.setBusinessName(updateBusinessNameRequest.getNewBusinessName());
        distributorRepository.save(distributor);

        response.setCode(InitConfig.SUCCESS);
        response.setTitle(InitConfig.TITLE_SUCCESS);
        response.setMessage("Distributor updated successfully.");
        return ResponseEntity.ok(response);
    }
}

