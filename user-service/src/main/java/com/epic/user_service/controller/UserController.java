package com.epic.user_service.controller;

import com.epic.user_service.dto.ChangePasswordReq;
import com.epic.user_service.dto.CommonResponse;
import com.epic.user_service.dto.UserLoginReq;
import com.epic.user_service.dto.UserRegisterReq;
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

    /**
     * // User registration API
     */
    @PostMapping("/register")
    public ResponseEntity<CommonResponse> registerUser(@RequestBody UserRegisterReq userRegisterReq){
        CommonResponse registerResponse = new CommonResponse();
        try{
            log.info("User register request is received");
            return userService.registerUser(userRegisterReq);

        } catch (Exception e){
            log.info("An error occurred :", e);

            registerResponse.setCode("Code");
            registerResponse.setTitle("Failed");
            registerResponse.setMessage("Error occurred in the registration : " + e.getMessage());
            return ResponseEntity.badRequest().body(registerResponse);
        }
    }

    /**
     * // User Login API
     */
    @PostMapping("/login")
    public ResponseEntity<CommonResponse> loginUser(@RequestBody UserLoginReq userLoginReq){
        CommonResponse loginResponse = new CommonResponse();
        try{
           log.info("User login request is received");
           return userService.loginUser(userLoginReq);

        } catch (Exception e){
           log.info("An error occurred :", e);

           loginResponse.setCode("Code");
           loginResponse.setTitle("Failed");
           loginResponse.setMessage("Error occurred in the login : " + e.getMessage());
           return ResponseEntity.badRequest().body(loginResponse);
        }
    }

    /**
     * // User Change Password API
     */
    @PutMapping("/change-pw")
    public ResponseEntity<CommonResponse> changePassword(@RequestBody ChangePasswordReq changePasswordReq){
        CommonResponse changePWResponse = new CommonResponse();
        try{
            log.info("Change password request received");
            return userService.changePassword(changePasswordReq);

        } catch (Exception e){
            log.info("An error occurred : ", e);

            changePWResponse.setCode("Code");
            changePWResponse.setTitle("Failed");
            changePWResponse.setMessage("Error occurred in the change password : " + e.getMessage());
            return ResponseEntity.badRequest().body(changePWResponse);
        }
    }

}
