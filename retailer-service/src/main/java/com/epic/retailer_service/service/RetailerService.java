package com.epic.retailer_service.service;

import com.epic.retailer_service.client.DistributorServiceClient;
import com.epic.retailer_service.client.OrderServiceClient;
import com.epic.retailer_service.client.UserServiceClient;
import com.epic.retailer_service.dto.*;
import com.epic.retailer_service.entity.AddDistributorReq;
import com.epic.retailer_service.entity.OrderRequest;
import com.epic.retailer_service.entity.Retailer;
import com.epic.retailer_service.exception.DistributorReqNotFoundException;
import com.epic.retailer_service.exception.OrderRequestNotFoundException;
import com.epic.retailer_service.exception.RetailerNotFoundException;
import com.epic.retailer_service.repository.AddDistributorReqRepository;
import com.epic.retailer_service.repository.OrderRequestRepository;
import com.epic.retailer_service.repository.RetailerRepository;
import com.epic.retailer_service.util.RequestValidator;
import com.epic.retailer_service.util.UniqueIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RetailerService {
    private final RetailerRepository retailerRepository;
    private final UserServiceClient userServiceClient;
    private final DistributorServiceClient distributorServiceClient;
    private final AddDistributorReqRepository addDistributorReqRepository;
    private final OrderRequestRepository orderRequestRepository;
    private final OrderServiceClient orderServiceClient;

    /**
     * // Retailer adding to the retailer table
     */
    public ResponseEntity<CommonResponse> addRetailer(AddRetailerReq addRetailerReq) {
        CommonResponse addRetailerResponse = new CommonResponse();

        Retailer retailer = new Retailer();
        String id = UniqueIdGenerator.generateRetailerId();

        retailer.setId(id);
        retailer.setUserId(addRetailerReq.getUserId());
        retailer.setStatus("INACTIVE");
        retailerRepository.save(retailer);

        addRetailerResponse.setCode("0000");
        addRetailerResponse.setTitle("Success");
        addRetailerResponse.setMessage("Distributor added to the table successfully");
        return ResponseEntity.ok(addRetailerResponse);
    }

    /**
     * // Update retailer details
     */
    public ResponseEntity<CommonResponse> updateRetailer(String authorizationHeader, UpdateRetailerReq updateRetailerReq){
        CommonResponse updateRetailerResponse = new CommonResponse();
        RequestValidator requestValidator = new RequestValidator();

        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.warn("Authorization header is missing or empty. Request cannot be processed.");

            updateRetailerResponse.setCode("Code");
            updateRetailerResponse.setTitle("Failed");
            updateRetailerResponse.setMessage("Token is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(updateRetailerResponse);
        }

        // Extract and validate the token
        String token = authorizationHeader.substring(7);
        boolean tokenValidity = requestValidator.validateReq(updateRetailerReq.getUsername(), "RETAILER", token);

        if(!tokenValidity){
            log.info("Token invalid or expired");

            updateRetailerResponse.setCode("Code");
            updateRetailerResponse.setTitle("Failed");
            updateRetailerResponse.setMessage("Token is Invalid or Expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(updateRetailerResponse);
        }

        SendUserIdRetrieve sendUserIdRetrieve = new SendUserIdRetrieve(updateRetailerReq.getUsername());

        try{
            String userId = userServiceClient.retrieveUserId(sendUserIdRetrieve).getBody().getUserData().getUserId();
            if(userId == null){
                log.info("User id is null");

                updateRetailerResponse.setCode("Code");
                updateRetailerResponse.setTitle("Failed");
                updateRetailerResponse.setMessage("Username is invalid");
                return ResponseEntity.badRequest().body(updateRetailerResponse);
            }

            Retailer existingRetailer = retailerRepository.findByUserId(userId).
                    orElseThrow(null);
            existingRetailer.setShopName(updateRetailerReq.getShopName());
            retailerRepository.save(existingRetailer);
            log.info("The details have been updated successfully");

            updateRetailerResponse.setCode("0000");
            updateRetailerResponse.setTitle("Success");
            updateRetailerResponse.setMessage("The retailer has been updated successfully");
            return ResponseEntity.ok(updateRetailerResponse);

        } catch (Exception e){
            log.info("Error occurred while sending the request to user-service", e);

            updateRetailerResponse.setCode("Code");
            updateRetailerResponse.setTitle("Failed");
            updateRetailerResponse.setMessage("Error occurred while processing the request");
            return ResponseEntity.badRequest().body(updateRetailerResponse);
        }
    }

    /**
     * // Requesting a distributor
     */
    public ResponseEntity<CommonResponse> requestDistributor(String authorizationHeader, DistributorRequest distributorRequest){
        CommonResponse requestDistributorResponse = new CommonResponse();
        SendUserIdRetrieve sendUserIdRetrieve = new SendUserIdRetrieve(distributorRequest.getUsername());
        RequestValidator requestValidator = new RequestValidator();

        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.warn("Authorization header is missing or empty. Request cannot be processed.");

            requestDistributorResponse.setCode("Code");
            requestDistributorResponse.setTitle("Failed");
            requestDistributorResponse.setMessage("Token is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(requestDistributorResponse);
        }

        // Extract and validate the token
        String token = authorizationHeader.substring(7);
        boolean tokenValidity = requestValidator.validateReq(distributorRequest.getUsername(), "RETAILER", token);

        if(!tokenValidity){
            log.info("Token invalid or expired");

            requestDistributorResponse.setCode("Code");
            requestDistributorResponse.setTitle("Failed");
            requestDistributorResponse.setMessage("Token is Invalid or Expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(requestDistributorResponse);
        }

        String userId = userServiceClient.retrieveUserId(sendUserIdRetrieve).getBody().getUserData().getUserId();
        if(userId == null){
            log.info("User id is null");

            requestDistributorResponse.setCode("Code");
            requestDistributorResponse.setTitle("Failed");
            requestDistributorResponse.setMessage("Username is invalid");
            return ResponseEntity.badRequest().body(requestDistributorResponse);
        }

        Retailer retailer = retailerRepository.findByUserId(userId).
                orElseThrow(null);
        SendDistributorReq sendDistributorReq = new SendDistributorReq();
        sendDistributorReq.setRetailerId(retailer.getId());
        sendDistributorReq.setRetailerUserId(userId);
        sendDistributorReq.setDistributorId(distributorRequest.getDistributorId());

        //saving the request in AddDistributorReq table
        AddDistributorReq addDistributorReq = new AddDistributorReq();
        addDistributorReq.setId(UniqueIdGenerator.generateUniqueId());
        addDistributorReq.setRetailerId(retailer.getId());
        addDistributorReq.setDistributorId(distributorRequest.getDistributorId());
        addDistributorReq.setStatus("PENDING");
        addDistributorReq.setRequestStatus("INITIALIZED");
        addDistributorReq.setCreatedTime(LocalDateTime.now());
        AddDistributorReq savedReq = addDistributorReqRepository.save(addDistributorReq);

        sendDistributorReq.setRetailerReqId(savedReq.getId());

        try{
            log.info("Request sending to distributor");
            String code = distributorServiceClient.requestDistributor(sendDistributorReq).getBody().getCode();

            if(code.equals("0000")){
                log.info("Distributor request was successful");

                AddDistributorReq existingReq = addDistributorReqRepository.findById(savedReq.getId()).
                        orElseThrow(null);
                existingReq.setRequestStatus("SENT TO DISTRIBUTOR");
                addDistributorReqRepository.save(existingReq);
                log.info("Request Status was updated");

                requestDistributorResponse.setCode("0000");
                requestDistributorResponse.setTitle("Success");
                requestDistributorResponse.setMessage("Request was sent to the distributor successfully");
                return ResponseEntity.ok(requestDistributorResponse);

            } else {
                log.info("Response was not successful");

                requestDistributorResponse.setCode("Code");
                requestDistributorResponse.setTitle("Failed");
                requestDistributorResponse.setMessage("Unsuccessful Response from Distributor");
                return ResponseEntity.badRequest().body(requestDistributorResponse);
            }
        } catch (Exception e){
            log.info("Error occurred while requesting distributor");

            requestDistributorResponse.setCode("Code");
            requestDistributorResponse.setTitle("Failed");
            requestDistributorResponse.setMessage("Error occurred while requesting distributor");
            return ResponseEntity.badRequest().body(requestDistributorResponse);
        }
    }

    /**
     * // Checking status of request sent to the distributor
     */
    public ResponseEntity<CommonResponse> requestDistributorStatusCheck(String authorizationHeader, DistributorRequestStatusCheck distributorRequestStatusCheck) {
        CommonResponse requestStatusCheckResponse = new CommonResponse();
        SendUserIdRetrieve sendUserIdRetrieve = new SendUserIdRetrieve(distributorRequestStatusCheck.getUsername());
        RequestValidator requestValidator = new RequestValidator();

        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.warn("Authorization header is missing or empty. Request cannot be processed.");

            requestStatusCheckResponse.setCode("Code");
            requestStatusCheckResponse.setTitle("Failed");
            requestStatusCheckResponse.setMessage("Token is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(requestStatusCheckResponse);
        }

        // Extract and validate the token
        String token = authorizationHeader.substring(7);
        boolean tokenValidity = requestValidator.validateReq(distributorRequestStatusCheck.getUsername(), "RETAILER", token);

        if(!tokenValidity){
            log.info("Token invalid or expired");

            requestStatusCheckResponse.setCode("Code");
            requestStatusCheckResponse.setTitle("Failed");
            requestStatusCheckResponse.setMessage("Token is Invalid or Expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(requestStatusCheckResponse);
        }

        //extract userId from user service
        String userId = userServiceClient.retrieveUserId(sendUserIdRetrieve).getBody().getUserData().getUserId();

        if(userId == null){
            log.info("User Id is null");

            requestStatusCheckResponse.setCode("Code");
            requestStatusCheckResponse.setTitle("Failed");
            requestStatusCheckResponse.setMessage("Username is invalid");
            return ResponseEntity.badRequest().body(requestStatusCheckResponse);
        }

        try{
            Retailer existingRetailer = retailerRepository.findByUserId(userId)
                    .orElseThrow(()-> new RetailerNotFoundException("Retailer Not Found"));
            String retailerId = existingRetailer.getId();
            log.info("Retailer Id is retrieved");

            if(!addDistributorReqRepository.findByRetailerId(retailerId).isEmpty()){
                log.info("Retailer found in the request table");

                requestStatusCheckResponse.setCode("0000");
                requestStatusCheckResponse.setTitle("Success");
                requestStatusCheckResponse.setMessage("Retailer's Requests retrieved Successfully");
                requestStatusCheckResponse.setReqList(addDistributorReqRepository.findByRetailerId(retailerId));
                return ResponseEntity.ok(requestStatusCheckResponse);

            } else {
                log.info("Retailer is not found in the request table");

                requestStatusCheckResponse.setCode("Code");
                requestStatusCheckResponse.setTitle("Failed");
                requestStatusCheckResponse.setMessage("Retailer has no any distributor Requests");
                return ResponseEntity.badRequest().body(requestStatusCheckResponse);
            }
        } catch (Exception e){
            log.info("Error occurred while sending the request", e);

            requestStatusCheckResponse.setCode("Code");
            requestStatusCheckResponse.setTitle("Failed");
            requestStatusCheckResponse.setMessage("Error sending the request");
            return ResponseEntity.badRequest().body(requestStatusCheckResponse);
        }
    }

    /**
     * // Updating the request status of Distributor request after distributors response
     */
    public ResponseEntity<CommonResponse> requestDistributorStatusUpdate(String authorizationHeader, DistributorRequestStatusUpdate distributorRequestStatusUpdate) {
        CommonResponse requestStatusUpdateResponse = new CommonResponse();
        RequestValidator requestValidator = new RequestValidator();

        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.warn("Authorization header is missing or empty. Request cannot be processed.");

            requestStatusUpdateResponse.setCode("Code");
            requestStatusUpdateResponse.setTitle("Failed");
            requestStatusUpdateResponse.setMessage("Token is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(requestStatusUpdateResponse);
        }

        // Extract and validate the token
        String token = authorizationHeader.substring(7);
        boolean tokenValidity = requestValidator.validateReq(distributorRequestStatusUpdate.getUsername(), "DISTRIBUTOR", token);

        if(!tokenValidity){
            log.info("Token invalid or expired");

            requestStatusUpdateResponse.setCode("Code");
            requestStatusUpdateResponse.setTitle("Failed");
            requestStatusUpdateResponse.setMessage("Token is Invalid or Expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(requestStatusUpdateResponse);
        }

        AddDistributorReq existingReq = addDistributorReqRepository.findById(distributorRequestStatusUpdate.getRetailerReqId())
                .orElseThrow(()-> new DistributorReqNotFoundException("Request Not Found"));

        if(distributorRequestStatusUpdate.getOption().equalsIgnoreCase("APPROVE")){
            log.info("Request Approved");
            existingReq.setStatus("APPROVED");
            existingReq.setRequestStatus("COMPLETED");
            existingReq.setUpdatedTime(LocalDateTime.now());
            addDistributorReqRepository.save(existingReq);

            requestStatusUpdateResponse.setCode("0000");
            requestStatusUpdateResponse.setTitle("Success");
            requestStatusUpdateResponse.setMessage("Approval Request Received Successfully");
            return ResponseEntity.ok(requestStatusUpdateResponse);

        } else if (distributorRequestStatusUpdate.getOption().equalsIgnoreCase("REJECT")) {
            log.info("Request Rejected");
            existingReq.setStatus("REJECTED");
            existingReq.setRequestStatus("COMPLETED");
            existingReq.setUpdatedTime(LocalDateTime.now());
            addDistributorReqRepository.save(existingReq);

            requestStatusUpdateResponse.setCode("0000");
            requestStatusUpdateResponse.setTitle("Success");
            requestStatusUpdateResponse.setMessage("Reject Request Received Successfully");
            return ResponseEntity.ok(requestStatusUpdateResponse);

        } else {
            log.info("Unidentified option");

            requestStatusUpdateResponse.setCode("Code");
            requestStatusUpdateResponse.setTitle("Failed");
            requestStatusUpdateResponse.setMessage("Unidentified Option from distributor");
            return ResponseEntity.ok(requestStatusUpdateResponse);
        }
    }

    /**
     * // Get all available distributors
     */
    public ResponseEntity<CommonResponse> getAllDistributors(String authorizationHeader, String username) {
        CommonResponse getDistributorsResponse = new CommonResponse();
        RequestValidator requestValidator = new RequestValidator();

        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.warn("Authorization header is missing or empty. Request cannot be processed.");

            getDistributorsResponse.setCode("Code");
            getDistributorsResponse.setTitle("Failed");
            getDistributorsResponse.setMessage("Token is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(getDistributorsResponse);
        }

        // Extract and validate the token
        String token = authorizationHeader.substring(7);
        boolean tokenValidity = requestValidator.validateReq(username, "RETAILER", token);

        if(!tokenValidity){
            log.info("Token invalid or expired");

            getDistributorsResponse.setCode("Code");
            getDistributorsResponse.setTitle("Failed");
            getDistributorsResponse.setMessage("Token is Invalid or Expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(getDistributorsResponse);
        }

        List<DistributorData> distributorList = distributorServiceClient.getAllDistributors().getBody().getDistributorList();

        if(distributorList == null || distributorList.isEmpty()){
            log.info("Distributor list is empty");

            getDistributorsResponse.setCode("Code");
            getDistributorsResponse.setTitle("Failed");
            getDistributorsResponse.setMessage("Distributor list is empty");
            return ResponseEntity.ok(getDistributorsResponse);
        }

        log.info("Distributor list found");

        getDistributorsResponse.setCode("0000");
        getDistributorsResponse.setTitle("Success");
        getDistributorsResponse.setMessage("Distributor list retrieved successfully");
        getDistributorsResponse.setDistributorList(distributorList);
        return ResponseEntity.ok(getDistributorsResponse);
    }

    /**
     * // Creating an order request
     */
    public ResponseEntity<CommonResponse> createOrderRequest(String authorizationHeader, CreateOrderReq createOrderReq){
        CommonResponse createOrderReqResponse = new CommonResponse();
        RequestValidator requestValidator = new RequestValidator();

        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.warn("Authorization header is missing or empty. Request cannot be processed.");

            createOrderReqResponse.setCode("Code");
            createOrderReqResponse.setTitle("Failed");
            createOrderReqResponse.setMessage("Token is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createOrderReqResponse);
        }

        // Extract and validate the token
        String token = authorizationHeader.substring(7);
        boolean tokenValidity = requestValidator.validateReq(createOrderReq.getUsername(), "RETAILER", token);

        if(!tokenValidity){
            log.info("Token invalid or expired");

            createOrderReqResponse.setCode("Code");
            createOrderReqResponse.setTitle("Failed");
            createOrderReqResponse.setMessage("Token is Invalid or Expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createOrderReqResponse);
        }

        SendUserIdRetrieve sendUserIdRetrieve = new SendUserIdRetrieve(createOrderReq.getUsername());
        String userId = userServiceClient.retrieveUserId(sendUserIdRetrieve).getBody().getUserData().getUserId();

        if(userId == null || userId.isEmpty()){
            log.info("User Id empty or null");

            createOrderReqResponse.setCode("Code");
            createOrderReqResponse.setTitle("Failed");
            createOrderReqResponse.setMessage("Invalid Username");
            return ResponseEntity.badRequest().body(createOrderReqResponse);
        }

        Retailer existingRetailer = retailerRepository.findByUserId(userId).
                orElseThrow(()-> new RetailerNotFoundException("Retailer Not Found"));
        String retailerId = existingRetailer.getId();

        //set the order req to database
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setId(UniqueIdGenerator.generateUniqueId());
        orderRequest.setRetailerId(retailerId);
        orderRequest.setDistributorId(createOrderReq.getDistributorId());
        orderRequest.setProductId(createOrderReq.getProductId());
        orderRequest.setProductCount(createOrderReq.getProductCount());
        orderRequest.setStatus("INITIALIZED");
        OrderRequest savedReq = orderRequestRepository.save(orderRequest);
        log.info("Order Req saved in database successfully");

        //set the sending req
        SendOrderReq sendOrderReq = new SendOrderReq();
        sendOrderReq.setOrderId(savedReq.getId());
        sendOrderReq.setRetailerId(savedReq.getRetailerId());
        sendOrderReq.setDistributorId(savedReq.getDistributorId());
        sendOrderReq.setProductId(savedReq.getProductId());
        sendOrderReq.setProductCount(savedReq.getProductCount());
        
        String code = orderServiceClient.receiveOrderReq(sendOrderReq).getBody().getCode();

        //todo: Introduce some other codes for specific errors
        if (code.equals("0000")){ //0000 means distributor service has received the req and stored it
            log.info("Request Success");

            OrderRequest existingReq = orderRequestRepository.findById(savedReq.getId())
                    .orElseThrow(null);
            existingReq.setStatus("RECEIVED BY DISTRIBUTOR");
            orderRequestRepository.save(existingReq);

            createOrderReqResponse.setCode("0000");
            createOrderReqResponse.setTitle("Success");
            createOrderReqResponse.setMessage("Order Request sent to the distributor successfully");
            return ResponseEntity.ok(createOrderReqResponse);

        } else {
            log.info("The request has failed");

            OrderRequest existingReq = orderRequestRepository.findById(savedReq.getId())
                    .orElseThrow(null);
            existingReq.setStatus("FAILED");
            orderRequestRepository.save(existingReq);

            createOrderReqResponse.setCode("Code");
            createOrderReqResponse.setTitle("Failed");
            createOrderReqResponse.setMessage("Request Failed");
            return ResponseEntity.badRequest().body(createOrderReqResponse);
        }
    }

    /**
     * // Check an order request status
     */
    public ResponseEntity<CommonResponse> checkOrderReqStatus(String authorizationHeader, CheckOrderReqStatus checkOrderReqStatus){
        CommonResponse checkOrderStatusResponse = new CommonResponse();
        RequestValidator requestValidator = new RequestValidator();

        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.warn("Authorization header is missing or empty. Request cannot be processed.");

            checkOrderStatusResponse.setCode("Code");
            checkOrderStatusResponse.setTitle("Failed");
            checkOrderStatusResponse.setMessage("Token is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(checkOrderStatusResponse);
        }

        // Extract and validate the token
        String token = authorizationHeader.substring(7);
        boolean tokenValidity = requestValidator.validateReq(checkOrderReqStatus.getUsername(), "RETAILER", token);

        if(!tokenValidity){
            log.info("Token invalid or expired");

            checkOrderStatusResponse.setCode("Code");
            checkOrderStatusResponse.setTitle("Failed");
            checkOrderStatusResponse.setMessage("Token is Invalid or Expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(checkOrderStatusResponse);
        }

        SendUserIdRetrieve sendUserIdRetrieve = new SendUserIdRetrieve(checkOrderReqStatus.getUsername());
        String userId = userServiceClient.retrieveUserId(sendUserIdRetrieve).getBody().getUserData().getUserId();

        if(userId == null || userId.isEmpty()){
            log.info("User Id empty or null");

            checkOrderStatusResponse.setCode("Code");
            checkOrderStatusResponse.setTitle("Failed");
            checkOrderStatusResponse.setMessage("Invalid Username");
            return ResponseEntity.badRequest().body(checkOrderStatusResponse);
        }

        //retrieve retailer Id
        Retailer existingRetailer = retailerRepository.findByUserId(userId).
                orElseThrow(()-> new RetailerNotFoundException("Retailer Not Found"));
        String retailerId = existingRetailer.getId();

        //retrieve all requests with retailer Id
        List<OrderRequest> orderRequestList = orderRequestRepository.findAll(retailerId);

        if(orderRequestList == null || orderRequestList.isEmpty()){
            log.info("List is empty");

            checkOrderStatusResponse.setCode("0000");
            checkOrderStatusResponse.setTitle("Success");
            checkOrderStatusResponse.setMessage("List Is Empty");
            return ResponseEntity.ok(checkOrderStatusResponse);
        }

        log.info("List is not empty");

        checkOrderStatusResponse.setCode("0000");
        checkOrderStatusResponse.setTitle("Success");
        checkOrderStatusResponse.setMessage("List retrieved successfully");
        checkOrderStatusResponse.setOrderReqList(orderRequestList);
        return ResponseEntity.ok(checkOrderStatusResponse);
    }

    /**
     * // Receiving an order request status update
     */
}
