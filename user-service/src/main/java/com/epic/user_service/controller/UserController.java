package com.epic.user_service.controller;

import com.epic.user_service.dto.CommonResponse;
import com.epic.user_service.dto.UserLoginReq;
import com.epic.user_service.dto.UserRegisterReq;
import com.epic.user_service.dto.admin.ChangeApprovalReq;
import com.epic.user_service.dto.admin.GetApprovalReq;
import com.epic.user_service.dto.admin.LoginAdminReq;
import com.epic.user_service.dto.retailer.RetrieveUserIdReq;
import com.epic.user_service.service.AdminService;
import com.epic.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AdminService adminService;
    /**
     * // User registration API
     */
    @PostMapping("/register")
    public ResponseEntity<CommonResponse> registerUser(@RequestBody UserRegisterReq userRegisterReq){
        log.info("User register request is received");
        return userService.registerUser(userRegisterReq);
    }

    /**
     * // User Login API
     */
    @PostMapping("/login")
    public ResponseEntity<CommonResponse> loginUser(@RequestBody UserLoginReq userLoginReq){
        log.info("User login request is received");
        return userService.loginUser(userLoginReq);
    }

    @PostMapping("/retrieveUserId")
    public ResponseEntity<CommonResponse> retrieveUserId(@RequestBody RetrieveUserIdReq retrieveUserIdReq){
        log.info("User Id Retrieval Request Received");
        return userService.retrieveUserId(retrieveUserIdReq);
    }

    @PostMapping("/approvalList")
    public ResponseEntity<CommonResponse> getApprovalList(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody GetApprovalReq getApprovalReq){
        return adminService.getApprovalPendingList(authorizationHeader, getApprovalReq);
    }

    @PostMapping("/approveOrReject")
    public ResponseEntity<CommonResponse> approveOrRejectRequest(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody ChangeApprovalReq changeApprovalReq){
        return adminService.approveOrRejectRequest(authorizationHeader, changeApprovalReq);
    }

    @PostMapping("/loginAdmin")
    public ResponseEntity<CommonResponse> loginAdmin(@RequestBody LoginAdminReq loginAdminReq){
        return adminService.loginAdmin(loginAdminReq);
    }

//    /**
//     * // User Change Password API
//     */
//    @PutMapping("/change-pw")
//    public ResponseEntity<CommonResponse> changePassword(@RequestBody ChangePasswordReq changePasswordReq){
//        log.info("Change password request received");
//        return userService.changePassword(changePasswordReq);
//    }
//
//    @PostMapping("/validate")
//    public String usernameValidityChecker(@RequestBody String username) {
//        return userService.validateUsernameNew(username);
//    }

}
