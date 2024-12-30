package com.example.DistributorService.Service;


import com.example.DistributorService.DTO.AddDistributorReq;
import com.example.DistributorService.DTO.CommonResponse;
import com.example.DistributorService.Entity.Distributor;
import com.example.DistributorService.DTO.DistributorData;
import com.example.DistributorService.Repository.DistributorRepository;
import com.example.DistributorService.Util.UniqueIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DistributorService {

    @Autowired
    private DistributorRepository distributorRepository;

    public ResponseEntity<CommonResponse> addDistributor(AddDistributorReq addDistributorReq) {
        CommonResponse addDistributorResponse = new CommonResponse();

        Distributor distributor = new Distributor();
        String distributorId = UniqueIdGenerator.generateDistributorId();

        distributor.setId(distributorId);
        distributor.setUserId(addDistributorReq.getUserId());
        distributor.setStatus("INACTIVE");
        distributorRepository.save(distributor);

        addDistributorResponse.setCode("0000");
        addDistributorResponse.setTitle("Success");
        addDistributorResponse.setMessage("Distributor added to the table successfully");
        return ResponseEntity.ok(addDistributorResponse);
    }

    public CommonResponse getAllDistributors() {
        CommonResponse response = new CommonResponse();
        try {
            // Retrieve all distributors from the repository
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
                response.setCode("200");
                response.setTitle("Success");
                response.setMessage("Distributors retrieved successfully.");
                response.setData(distributorDataList);
            } else {
                // Handle the case when no distributors are found
                response.setCode("404");
                response.setTitle("No Data");
                response.setMessage("No distributors found.");
                response.setData(null);
            }
        } catch (Exception e) {
            // Handle exceptions and set error response details
            response.setCode("500");
            response.setTitle("Failed");
            response.setMessage("An error occurred while retrieving distributors: " + e.getMessage());
            response.setData(null);
        }

        return response;
    }

}

