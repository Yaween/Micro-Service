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

            getApprovalListResponse.setCode(InitConfig.TOKEN_MISSING);
            getApprovalListResponse.setTitle(InitConfig.TITLE_UNAUTHORIZED);
            getApprovalListResponse.setMessage("Token is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(getApprovalListResponse);
        }

        if(userRepository.findByUsername(getApprovalReq.getUsername()).isEmpty()){
            log.info("admin not found");

            getApprovalListResponse.setCode(InitConfig.ADMIN_NOT_FOUND);
            getApprovalListResponse.setTitle(InitConfig.TITLE_FAILED);
            getApprovalListResponse.setMessage("Admin with that username is not found");
            return ResponseEntity.badRequest().body(getApprovalListResponse);
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

            if(filteredList.isEmpty()){
                getApprovalListResponse.setCode(InitConfig.SUCCESS);
                getApprovalListResponse.setTitle(InitConfig.TITLE_SUCCESS);
                getApprovalListResponse.setMessage("No Any Pending Requests");
                return ResponseEntity.ok(getApprovalListResponse);
            }

            getApprovalListResponse.setCode(InitConfig.SUCCESS);
            getApprovalListResponse.setTitle(InitConfig.TITLE_SUCCESS);
            getApprovalListResponse.setMessage("List retrieved Successfully");
            getApprovalListResponse.setApprovalList(filteredList);
            return ResponseEntity.ok(getApprovalListResponse);

        } else {
            log.info("Token invalid or expired");

            getApprovalListResponse.setCode(InitConfig.TOKEN_INVALID_EXPIRED);
            getApprovalListResponse.setTitle(InitConfig.TITLE_UNAUTHORIZED);
            getApprovalListResponse.setMessage("Token is Invalid or Expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(getApprovalListResponse);
        }
    }

    public ResponseEntity<CommonResponse> approveOrRejectRequest(String authorizationHeader, ChangeApprovalReq changeApprovalReq){
        CommonResponse approveOrRejectResponse = new CommonResponse();
        RequestValidator requestValidator = new RequestValidator();

        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.warn("Authorization header is missing or empty. Request cannot be processed.");

            approveOrRejectResponse.setCode(InitConfig.TOKEN_MISSING);
            approveOrRejectResponse.setTitle(InitConfig.TITLE_UNAUTHORIZED);
            approveOrRejectResponse.setMessage("Token is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(approveOrRejectResponse);
        }

        // Extract and validate the token
        String token = authorizationHeader.substring(7);
        boolean tokenValidity = requestValidator.validateReq(changeApprovalReq.getAdminUsername(), "ADMIN", token);

        if(!tokenValidity){
            log.info("Token is invalid or expired");

            approveOrRejectResponse.setCode(InitConfig.TOKEN_INVALID_EXPIRED);
            approveOrRejectResponse.setTitle(InitConfig.TITLE_UNAUTHORIZED);
            approveOrRejectResponse.setMessage("Token is Invalid or Expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(approveOrRejectResponse);
        }

        String command = changeApprovalReq.getStatusCommand();

        if(userApprovalRepository.findById(changeApprovalReq.getApprovalReqId()).isEmpty()){
            log.info("Approval request id is missing");

            approveOrRejectResponse.setCode(InitConfig.REQUEST_NOT_FOUND);
            approveOrRejectResponse.setTitle(InitConfig.TITLE_FAILED);
            approveOrRejectResponse.setMessage("Approval request not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(approveOrRejectResponse);
        }

        if(command.equalsIgnoreCase("approve")){
            log.info("Approve flow");

            UserApproval existingReq = userApprovalRepository.findById(changeApprovalReq.getApprovalReqId())
                    .orElseThrow(null);

            if(existingReq.getStatus().equalsIgnoreCase("pending")){
                existingReq.setStatus("APPROVED");

                User adminUser = userRepository.findByUsername(changeApprovalReq.getAdminUsername())
                        .orElseThrow(()-> new UsernameNotFoundException("Username Not Found"));
                String adminUserId = adminUser.getId();
                existingReq.setAdminUserId(adminUserId);
                userApprovalRepository.save(existingReq);

                User existingUser = userRepository.findByUsername(existingReq.getUsername())
                        .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
                existingUser.setUpdatedTime(LocalDateTime.now());
                existingUser.setStatus("APPROVED");
                userRepository.save(existingUser);

                if(existingReq.getUserType().equalsIgnoreCase("RETAILER")){
                    SendRetailerAddReq sendRetailerAddReq = new SendRetailerAddReq();
                    sendRetailerAddReq.setUserId(existingUser.getId());

                    try{
                        String code = retailerServiceClient.addRetailer(sendRetailerAddReq).getBody().getCode();

                        if(code.equalsIgnoreCase("0000")){

                            approveOrRejectResponse.setCode(InitConfig.SUCCESS);
                            approveOrRejectResponse.setTitle(InitConfig.TITLE_SUCCESS);
                            approveOrRejectResponse.setMessage("Request was approved successfully");
                            return ResponseEntity.ok(approveOrRejectResponse);

                        } else {
                            log.error("Failed response from retailer service");

                            approveOrRejectResponse.setCode(code);
                            approveOrRejectResponse.setTitle(InitConfig.TITLE_FAILED);
                            approveOrRejectResponse.setMessage("Error when adding the retailer");
                            return ResponseEntity.badRequest().body(approveOrRejectResponse);

                        }
                    } catch (Exception e){
                        log.info("Request Failed");

                        approveOrRejectResponse.setCode(InitConfig.REQUEST_FAILED);
                        approveOrRejectResponse.setTitle(InitConfig.TITLE_FAILED);
                        approveOrRejectResponse.setMessage("Request was failed");
                        return ResponseEntity.badRequest().body(approveOrRejectResponse);
                    }
                } else if (existingReq.getUserType().equalsIgnoreCase("DISTRIBUTOR")) {

                    SendDistributorAddReq sendDistributorAddReq = new SendDistributorAddReq();
                    sendDistributorAddReq.setUserId(existingUser.getId());

                    try{
                        String code = distributorServiceClient.addDistributor(sendDistributorAddReq).getBody().getCode();

                        if(code.equalsIgnoreCase("0000")){

                            approveOrRejectResponse.setCode(InitConfig.SUCCESS);
                            approveOrRejectResponse.setTitle(InitConfig.TITLE_SUCCESS);
                            approveOrRejectResponse.setMessage("Request was approved successfully");
                            return ResponseEntity.ok(approveOrRejectResponse);

                        } else {
                            log.error("Failed response from distributor service");

                            approveOrRejectResponse.setCode(code);
                            approveOrRejectResponse.setTitle(InitConfig.TITLE_FAILED);
                            approveOrRejectResponse.setMessage("Error when adding the distributor");
                            return ResponseEntity.badRequest().body(approveOrRejectResponse);

                        }
                    } catch (Exception e){
                        log.info("Request Failed");

                        approveOrRejectResponse.setCode(InitConfig.REQUEST_FAILED);
                        approveOrRejectResponse.setTitle(InitConfig.TITLE_FAILED);
                        approveOrRejectResponse.setMessage("Request was failed");
                        return ResponseEntity.badRequest().body(approveOrRejectResponse);
                    }

                } else {
                    log.error("The user type is invalid");

                    approveOrRejectResponse.setCode(InitConfig.ROLE_INVALID);
                    approveOrRejectResponse.setTitle(InitConfig.TITLE_FAILED);
                    approveOrRejectResponse.setMessage("Invalid User Type");
                    return ResponseEntity.badRequest().body(approveOrRejectResponse);
                }

            } else {
                log.info("Req has already changed");

                approveOrRejectResponse.setCode(InitConfig.REQUEST_ALREADY_ALTERED);
                approveOrRejectResponse.setTitle(InitConfig.TITLE_FAILED);
                approveOrRejectResponse.setMessage("Request has already being altered");
                return ResponseEntity.badRequest().body(approveOrRejectResponse);
            }
        } else if (command.equalsIgnoreCase("reject")) {
            log.info("Reject flow");

            UserApproval existingReq = userApprovalRepository.findById(changeApprovalReq.getApprovalReqId())
                    .orElseThrow(null);

            if(existingReq.getStatus().equalsIgnoreCase("pending")){
                existingReq.setStatus("REJECTED");
                User adminUser = userRepository.findByUsername(changeApprovalReq.getAdminUsername())
                        .orElseThrow(()-> new UsernameNotFoundException("Username Not Found"));
                String adminUserId = adminUser.getId();
                existingReq.setAdminUserId(adminUserId);
                userApprovalRepository.save(existingReq);

                User existingUser = userRepository.findByUsername(existingReq.getUsername())
                        .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
                existingUser.setUpdatedTime(LocalDateTime.now());
                existingUser.setStatus("REJECTED");
                userRepository.save(existingUser);

                approveOrRejectResponse.setCode(InitConfig.SUCCESS);
                approveOrRejectResponse.setTitle(InitConfig.TITLE_SUCCESS);
                approveOrRejectResponse.setMessage("Request was rejected successfully");
                return ResponseEntity.ok(approveOrRejectResponse);

            } else {
                log.info("Req has already changed");

                approveOrRejectResponse.setCode(InitConfig.REQUEST_ALREADY_ALTERED);
                approveOrRejectResponse.setTitle(InitConfig.TITLE_FAILED);
                approveOrRejectResponse.setMessage("Request has already being altered");
                return ResponseEntity.badRequest().body(approveOrRejectResponse);
            }

        } else {
            log.info("Invalid Command");

            approveOrRejectResponse.setCode(InitConfig.INVALID_COMMAND);
            approveOrRejectResponse.setTitle(InitConfig.TITLE_FAILED);
            approveOrRejectResponse.setMessage("Invalid Command");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(approveOrRejectResponse);
        }
    }

    public ResponseEntity<CommonResponse> loginAdmin(LoginAdminReq loginAdminReq){
        CommonResponse adminLoginResponse = new CommonResponse();
        JWTGenerator jwtGenerator = new JWTGenerator();

        if(loginAdminReq.getUsername() == null || loginAdminReq.getUsername().isEmpty()
                || loginAdminReq.getPassword() == null || loginAdminReq.getPassword().isEmpty()){
            log.info("Request is missing values");

            adminLoginResponse.setCode(InitConfig.MISSING_FIELDS);
            adminLoginResponse.setTitle(InitConfig.TITLE_FAILED);
            adminLoginResponse.setMessage("Missing Values in the request");
            return ResponseEntity.badRequest().body(adminLoginResponse);
        }

        if(userRepository.findByUsername(loginAdminReq.getUsername()).isPresent()){
            log.info("User found");

            User existingUser = userRepository.findByUsername(loginAdminReq.getUsername())
                    .orElseThrow(()-> new UsernameNotFoundException("Username Not Found"));

            if(!existingUser.getUserType().equalsIgnoreCase("ADMIN")){
                log.info("User is not an Admin");

                adminLoginResponse.setCode(InitConfig.ADMIN_NOT_FOUND);
                adminLoginResponse.setTitle(InitConfig.TITLE_FAILED);
                adminLoginResponse.setMessage("User is not an Admin");
                return ResponseEntity.badRequest().body(adminLoginResponse);
            }
            String savedPassword = existingUser.getPassword();

            if(PasswordUtil.isPasswordValid(loginAdminReq.getPassword(), savedPassword)){
                log.info("Password correct");

                String token = jwtGenerator.generateToken(loginAdminReq.getUsername(), "ADMIN");
                log.info("Token Generated");

                adminLoginResponse.setCode(InitConfig.SUCCESS);
                adminLoginResponse.setTitle(InitConfig.TITLE_SUCCESS);
                adminLoginResponse.setMessage("Login Successful and Token generated");
                adminLoginResponse.setToken(new TokenData(token));

            } else {
                log.info("Password incorrect");

                adminLoginResponse.setCode(InitConfig.PASSWORD_INCORRECT);
                adminLoginResponse.setTitle(InitConfig.TITLE_FAILED);
                adminLoginResponse.setMessage("Password entered is incorrect");
            }
            return ResponseEntity.ok(adminLoginResponse);

        } else {
            log.info("User not found");

            adminLoginResponse.setCode(InitConfig.USERNAME_NOT_FOUND);
            adminLoginResponse.setTitle(InitConfig.TITLE_FAILED);
            adminLoginResponse.setMessage("User with the give username is not found in the system");
            return ResponseEntity.badRequest().body(adminLoginResponse);
        }
    }
}
