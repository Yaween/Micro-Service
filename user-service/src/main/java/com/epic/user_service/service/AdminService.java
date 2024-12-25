package com.epic.user_service.service;

import com.epic.user_service.client.DistributorServiceClient;
import com.epic.user_service.client.RetailerServiceClient;
import com.epic.user_service.config.InitConfig;
import com.epic.user_service.dto.CommonResponse;
import com.epic.user_service.dto.TokenData;
import com.epic.user_service.dto.admin.*;
import com.epic.user_service.entity.User;
import com.epic.user_service.entity.UserApproval;
import com.epic.user_service.exception.UsernameNotFoundException;
import com.epic.user_service.repository.UserApprovalRepository;
import com.epic.user_service.repository.UserRepository;
import com.epic.user_service.util.JWTGenerator;
import com.epic.user_service.util.PasswordUtil;
import com.epic.user_service.util.RequestValidator;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final UserRepository userRepository;
    private final UserApprovalRepository userApprovalRepository;
    private final DistributorServiceClient distributorServiceClient;
    private final RetailerServiceClient retailerServiceClient;

    public ResponseEntity<CommonResponse> getApprovalPendingList(String authorizationHeader, GetApprovalReq getApprovalReq){
        CommonResponse getApprovalListResponse = new CommonResponse();
        RequestValidator requestValidator = new RequestValidator();

        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.warn("Authorization header is missing or empty. Request cannot be processed.");

            getApprovalListResponse.setCode("Code");
            getApprovalListResponse.setTitle("Failed");
            getApprovalListResponse.setMessage("Token is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(getApprovalListResponse);
        }

        // Extract and validate the token
        String token = authorizationHeader.substring(7);
        boolean tokenValidity = requestValidator.validateReq(getApprovalReq.getUsername(), "ADMIN", token);

        if(tokenValidity){
            log.info("Token is valid");

            List<UserApproval> userApprovalList = userApprovalRepository.findAll();

            List<Map<String, Object>> filteredList = new ArrayList<>();
            for (UserApproval userApproval : userApprovalList) {
                Map<String, Object> map = new HashMap<>();
                if(userApproval.getStatus().equalsIgnoreCase("PENDING")){
                    map.put("id", userApproval.getId());
                    map.put("userId", userApproval.getUserId());
                    map.put("userType", userApproval.getUserType());
                    map.put("username", userApproval.getUsername());
                    filteredList.add(map);
                }
            }

            getApprovalListResponse.setCode("0000");
            getApprovalListResponse.setTitle("Success");
            getApprovalListResponse.setMessage("List retrieved Successfully");
            getApprovalListResponse.setApprovalList(filteredList);
            return ResponseEntity.ok(getApprovalListResponse);

        } else {
            log.info("Token invalid or expired");

            getApprovalListResponse.setCode("Code");
            getApprovalListResponse.setTitle("Failed");
            getApprovalListResponse.setMessage("Token is Invalid or Expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(getApprovalListResponse);
        }
    }

    public ResponseEntity<CommonResponse> approveOrRejectRequest(String authorizationHeader, ChangeApprovalReq changeApprovalReq){
        CommonResponse approveOrRejectResponse = new CommonResponse();
        RequestValidator requestValidator = new RequestValidator();

        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.warn("Authorization header is missing or empty. Request cannot be processed.");

            approveOrRejectResponse.setCode("Code");
            approveOrRejectResponse.setTitle("Failed");
            approveOrRejectResponse.setMessage("Token is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(approveOrRejectResponse);
        }

        // Extract and validate the token
        String token = authorizationHeader.substring(7);
        boolean tokenValidity = requestValidator.validateReq(changeApprovalReq.getAdminUsername(), "ADMIN", token);

        if(!tokenValidity){
            log.info("Token is invalid or expired");

            approveOrRejectResponse.setCode("Code");
            approveOrRejectResponse.setTitle("Failed");
            approveOrRejectResponse.setMessage("Token is Invalid or Expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(approveOrRejectResponse);
        }

        if(userRepository.findByUsername(changeApprovalReq.getAdminUsername()).isPresent()){
            log.info("Admin was found");

            if (userApprovalRepository.findById(changeApprovalReq.getApprovalReqId()).isPresent()){
                log.info("Approval request found");

                UserApproval approvalReq = userApprovalRepository.findById(changeApprovalReq.getApprovalReqId()).
                        orElseThrow(null);

                if(approvalReq.getStatus().equalsIgnoreCase("PENDING")){
                    log.info("Status is pending");

                    User adminUser = userRepository.findByUsername(changeApprovalReq.getAdminUsername()).
                            orElseThrow(null);
                    String adminUserId = adminUser.getId();

                    if(changeApprovalReq.getStatusCommand().equalsIgnoreCase("APPROVE")){
                        log.info("request is approving");

                        User existingUser = userRepository.findById(approvalReq.getUserId()).
                                orElseThrow(null);

                        if(existingUser.getUserType().equalsIgnoreCase("DISTRIBUTOR")){
                            SendDistributorAddReq sendDistributorAddReq = new SendDistributorAddReq();
                            sendDistributorAddReq.setUserId(existingUser.getId());

                            String code = distributorServiceClient.addDistributor(sendDistributorAddReq).getBody().getCode();

                            if (code.equals("0000")){
                                approvalReq.setStatus("APPROVED");
                                approvalReq.setAdminUserId(adminUserId);
                                userApprovalRepository.save(approvalReq);

                                existingUser.setStatus("APPROVED");
                                existingUser.setUpdatedTime(LocalDateTime.now());
                                userRepository.save(existingUser);

                                approveOrRejectResponse.setCode("0000");
                                approveOrRejectResponse.setTitle("Success");
                                approveOrRejectResponse.setMessage("Request was approved and added as distributor");

                            } else {
                                approveOrRejectResponse.setCode("Code");
                                approveOrRejectResponse.setTitle("Failed");
                                approveOrRejectResponse.setMessage("Request was approved but failed adding as distributor");
                            }
                            return ResponseEntity.ok(approveOrRejectResponse);

                        } else if (existingUser.getUserType().equalsIgnoreCase("RETAILER")) {
                            SendRetailerAddReq sendRetailerAddReq = new SendRetailerAddReq();
                            sendRetailerAddReq.setUserId(existingUser.getId());

                            String code = retailerServiceClient.addRetailer(sendRetailerAddReq).getBody().getCode();

                            if (code.equals("0000")){
                                approveOrRejectResponse.setCode("0000");
                                approveOrRejectResponse.setTitle("Success");
                                approveOrRejectResponse.setMessage("Request was approved and added as retailer");

                            } else {
                                approveOrRejectResponse.setCode("Code");
                                approveOrRejectResponse.setTitle("Failed");
                                approveOrRejectResponse.setMessage("Request was approved but failed adding as retailer");
                            }
                            return ResponseEntity.ok(approveOrRejectResponse);

                        } else {
                            //todo: Complete the flow
                            return null;
                        }
                    } else if (changeApprovalReq.getStatusCommand().equalsIgnoreCase("REJECT")) {
                        log.info("request is rejecting");

                        approvalReq.setStatus("REJECTED");
                        approvalReq.setAdminUserId(adminUserId);
                        userApprovalRepository.save(approvalReq);

                        User existingUser = userRepository.findById(approvalReq.getUserId()).
                                orElseThrow(null);
                        existingUser.setStatus("REJECTED");
                        existingUser.setUpdatedTime(LocalDateTime.now());
                        userRepository.save(existingUser);

                        approveOrRejectResponse.setCode("Code");
                        approveOrRejectResponse.setTitle("Failed");
                        approveOrRejectResponse.setMessage("Request was rejected");
                        return ResponseEntity.ok(approveOrRejectResponse);

                    } else {
                        log.info("Unidentified status command");

                        approveOrRejectResponse.setCode("Code");
                        approveOrRejectResponse.setTitle("Failed");
                        approveOrRejectResponse.setMessage("Status command is invalid");
                        return ResponseEntity.ok(approveOrRejectResponse);
                    }

                } else {
                    log.info("Request is already rejected or approved");

                    approveOrRejectResponse.setCode("Code");
                    approveOrRejectResponse.setTitle("Failed");
                    approveOrRejectResponse.setMessage("Approval Request has already being changed");
                    return ResponseEntity.ok(approveOrRejectResponse);
                }
            } else {
                log.info("Approval Request was not found");

                approveOrRejectResponse.setCode("Code");
                approveOrRejectResponse.setTitle("Failed");
                approveOrRejectResponse.setMessage("Approval request with the give id is not found");
                return ResponseEntity.badRequest().body(approveOrRejectResponse);
            }
        } else {
            log.info("Admin not found for the given Id");

            approveOrRejectResponse.setCode("Code");
            approveOrRejectResponse.setTitle("Failed");
            approveOrRejectResponse.setMessage("Admin with the given id is not found");
            return ResponseEntity.badRequest().body(approveOrRejectResponse);
        }
    }

    public ResponseEntity<CommonResponse> loginAdmin(LoginAdminReq loginAdminReq){
        CommonResponse adminLoginResponse = new CommonResponse();
        JWTGenerator jwtGenerator = new JWTGenerator();

        if(loginAdminReq.getUsername() == null || loginAdminReq.getUsername().isEmpty()
                || loginAdminReq.getPassword() == null || loginAdminReq.getPassword().isEmpty()){
            log.info("Request is missing values");

            adminLoginResponse.setCode("Code");
            adminLoginResponse.setTitle("Failed");
            adminLoginResponse.setMessage("Missing Values in the request");
            return ResponseEntity.badRequest().body(adminLoginResponse);
        }

        if(userRepository.findByUsername(loginAdminReq.getUsername()).isPresent()){
            log.info("User found");

            User existingUser = userRepository.findByUsername(loginAdminReq.getUsername())
                    .orElseThrow(()-> new UsernameNotFoundException("Username Not Found"));

            if(!existingUser.getUserType().equalsIgnoreCase("ADMIN")){
                log.info("User is not an Admin");

                adminLoginResponse.setCode("Code");
                adminLoginResponse.setTitle("Failed");
                adminLoginResponse.setMessage("User is not an Admin");
                return ResponseEntity.badRequest().body(adminLoginResponse);
            }
            String savedPassword = existingUser.getPassword();

            if(PasswordUtil.isPasswordValid(loginAdminReq.getPassword(), savedPassword)){
                log.info("Password correct");

                String token = jwtGenerator.generateToken(loginAdminReq.getUsername(), "ADMIN");
                log.info("Token Generated");

                adminLoginResponse.setCode("0000");
                adminLoginResponse.setTitle("Success");
                adminLoginResponse.setMessage("Login Successful and Token generated");
                adminLoginResponse.setToken(new TokenData(token));

            } else {
                log.info("Password incorrect");

                adminLoginResponse.setCode("Code");
                adminLoginResponse.setTitle("Failed");
                adminLoginResponse.setMessage("Password entered is incorrect");
            }
            return ResponseEntity.ok(adminLoginResponse);

        } else {
            log.info("User not found");

            adminLoginResponse.setCode("Code");
            adminLoginResponse.setTitle("Failed");
            adminLoginResponse.setMessage("User with the give username is not found in the system");
            return ResponseEntity.badRequest().body(adminLoginResponse);
        }
    }
}
