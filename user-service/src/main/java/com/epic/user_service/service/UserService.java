package com.epic.user_service.service;

import com.epic.user_service.config.InitConfig;
import com.epic.user_service.dto.*;
import com.epic.user_service.dto.retailer.RetrieveUserIdReq;
import com.epic.user_service.dto.retailer.UserData;
import com.epic.user_service.entity.User;
import com.epic.user_service.entity.UserApproval;
import com.epic.user_service.exception.UsernameNotFoundException;
import com.epic.user_service.repository.UserApprovalRepository;
import com.epic.user_service.repository.UserRepository;
import com.epic.user_service.util.JWTGenerator;
import com.epic.user_service.util.PasswordUtil;
import com.epic.user_service.util.RequestNullChecker;
import com.epic.user_service.util.UniqueIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserApprovalRepository userApprovalRepository;

    /**
     * // User registration method
     */
    public ResponseEntity<CommonResponse> registerUser(UserRegisterReq userRegisterReq) {
       CommonResponse registerResponse = new CommonResponse();
       RequestNullChecker requestNullChecker = new RequestNullChecker();

        if (requestNullChecker.isNullOrEmpty(userRegisterReq.getUsername(), userRegisterReq.getPassword(),
                userRegisterReq.getFirstName(), userRegisterReq.getLastName(),
                userRegisterReq.getEmail(), userRegisterReq.getContactNumber())) {

            log.info("Fields are missing in the request");

            registerResponse.setCode(InitConfig.MISSING_FIELDS);
            registerResponse.setTitle(InitConfig.TITLE_FAILED);
            registerResponse.setMessage("All fields are required.");
            return ResponseEntity.badRequest().body(registerResponse);
        }

        //checks whether the username is already existing
        if(userRepository.findByUsername(userRegisterReq.getUsername()).isPresent()){
            log.info("Username is already taken");

            registerResponse.setCode(InitConfig.USERNAME_TAKEN);
            registerResponse.setTitle(InitConfig.TITLE_FAILED);
            registerResponse.setMessage("Username is already taken.");
            return ResponseEntity.badRequest().body(registerResponse);
        }

        //checks whether the email is already existing
        if(userRepository.findByEmail(userRegisterReq.getEmail()).isPresent()){
            log.info("Email is already taken");

            registerResponse.setCode(InitConfig.EMAIL_TAKEN);
            registerResponse.setTitle(InitConfig.TITLE_FAILED);
            registerResponse.setMessage("A User is already registered with that email");
            return ResponseEntity.badRequest().body(registerResponse);
        }

        //checks whether the contact number is already existing
        if(userRepository.findByContactNumber(userRegisterReq.getContactNumber()).isPresent()){
            log.info("Contact Number is already taken");

            registerResponse.setCode(InitConfig.CONTACT_NO_TAKEN);
            registerResponse.setTitle(InitConfig.TITLE_FAILED);
            registerResponse.setMessage("A User is already registered with that number");
            return ResponseEntity.badRequest().body(registerResponse);
        }

        log.info("User is validated with existing users");

        String userId = UniqueIdGenerator.generateUniqueId();

        User user = new User();
        user.setId(userId);
        user.setFirstName(userRegisterReq.getFirstName());
        user.setLastName(userRegisterReq.getLastName());
        user.setEmail(userRegisterReq.getEmail());
        user.setContactNumber(userRegisterReq.getContactNumber());

        if(userRegisterReq.getUserType().equalsIgnoreCase("DISTRIBUTOR")){
            user.setUserType("DISTRIBUTOR");

        } else if (userRegisterReq.getUserType().equalsIgnoreCase("RETAILER")) {
            user.setUserType("RETAILER");

        } else if (userRegisterReq.getUserType().equalsIgnoreCase("ADMIN")) {
            user.setUserType("ADMIN");

            //save admin without approval
            user.setUsername(userRegisterReq.getUsername());
            String newPassword = PasswordUtil.encodePassword(userRegisterReq.getPassword());
            user.setPassword(newPassword);
            user.setCreatedTime(LocalDateTime.now());
            userRepository.save(user);

            registerResponse.setCode(InitConfig.SUCCESS);
            registerResponse.setTitle(InitConfig.TITLE_SUCCESS);
            registerResponse.setMessage("Admin User Registered Successfully");
            return ResponseEntity.ok(registerResponse);

        } else {
            log.info("Role is invalid");

            registerResponse.setCode(InitConfig.ROLE_INVALID);
            registerResponse.setTitle(InitConfig.TITLE_FAILED);
            registerResponse.setMessage("Role is invalid.");
            return ResponseEntity.badRequest().body(registerResponse);
        }

        user.setUsername(userRegisterReq.getUsername());
        String newPassword = PasswordUtil.encodePassword(userRegisterReq.getPassword());
        user.setPassword(newPassword);
        user.setCreatedTime(LocalDateTime.now());
        user.setStatus("PENDING");
        User saveduser = userRepository.save(user);

        //save in the admin approval table
        UserApproval userApproval = new UserApproval();
        userApproval.setUserId(userId);
        userApproval.setStatus("PENDING");
        userApproval.setUserType(saveduser.getUserType());
        userApproval.setUsername(userRegisterReq.getUsername());
        userApprovalRepository.save(userApproval);

        //return response
        registerResponse.setCode(InitConfig.SUCCESS);
        registerResponse.setTitle(InitConfig.TITLE_SUCCESS);
        registerResponse.setMessage("Request was sent to the admin for the approval");
        return ResponseEntity.ok(registerResponse);
    }

    /**
     * // User login method
     */
    public ResponseEntity<CommonResponse> loginUser(UserLoginReq userLoginReq){
        CommonResponse userLoginResponse = new CommonResponse();
        JWTGenerator jwtGenerator = new JWTGenerator();

        if(userLoginReq.getUsername() == null || userLoginReq.getUsername().isEmpty()
                || userLoginReq.getPassword() == null || userLoginReq.getPassword().isEmpty()){
            log.info("Request is missing values");

            userLoginResponse.setCode(InitConfig.MISSING_FIELDS);
            userLoginResponse.setTitle(InitConfig.TITLE_FAILED);
            userLoginResponse.setMessage("Missing Values in the request");
            return ResponseEntity.badRequest().body(userLoginResponse);
        }

        if(userRepository.findByUsername(userLoginReq.getUsername()).isPresent()){
            log.info("User is found");

            User existingUser = userRepository.findByUsername(userLoginReq.getUsername())
                    .orElseThrow(()-> new UsernameNotFoundException("Username Not Found"));
            String savedPassword = existingUser.getPassword();
            String userType = existingUser.getUserType();
            String status = existingUser.getStatus();

            if(status.equalsIgnoreCase("PENDING")){
                log.info("User cant login till req is approved");

                userLoginResponse.setCode(InitConfig.REGISTRATION_REQ_PENDING);
                userLoginResponse.setTitle(InitConfig.TITLE_FAILED);
                userLoginResponse.setMessage("User cant login until an admin approve the registration request");
                return ResponseEntity.ok(userLoginResponse);

            } else if(status.equalsIgnoreCase("REJECTED")){
                log.info("User cant login if req is rejected");

                userLoginResponse.setCode(InitConfig.REGISTRATION_REQ_FAILED);
                userLoginResponse.setTitle(InitConfig.TITLE_FAILED);
                userLoginResponse.setMessage("You registration request is rejected. Login Failed");
                return ResponseEntity.ok(userLoginResponse);
            }

            if(PasswordUtil.isPasswordValid(userLoginReq.getPassword(), savedPassword)){
                log.info("Password correct");

                String token = jwtGenerator.generateToken(userLoginReq.getUsername(), userType);
                log.info("Token Generated");

                userLoginResponse.setCode(InitConfig.SUCCESS);
                userLoginResponse.setTitle(InitConfig.TITLE_SUCCESS);
                userLoginResponse.setMessage("Login Successful and Token generated");
                userLoginResponse.setToken(new TokenData(token));

            } else {
                log.info("Password incorrect");

                userLoginResponse.setCode(InitConfig.PASSWORD_INCORRECT);
                userLoginResponse.setTitle(InitConfig.TITLE_FAILED);
                userLoginResponse.setMessage("Password entered is incorrect");
            }
            return ResponseEntity.ok(userLoginResponse);

        } else {
            log.info("User not found");

            userLoginResponse.setCode(InitConfig.USERNAME_NOT_FOUND);
            userLoginResponse.setTitle(InitConfig.TITLE_FAILED);
            userLoginResponse.setMessage("User with the give username is not found in the system");
            return ResponseEntity.badRequest().body(userLoginResponse);
        }
    }

    public ResponseEntity<CommonResponse> retrieveUserId(RetrieveUserIdReq retrieveUserIdReq) {
        CommonResponse retrieveUserIdResponse = new CommonResponse();

        if(userRepository.findByUsername(retrieveUserIdReq.getUsername()).isPresent()){
            log.info("User found");

            User exisitingUser = userRepository.findByUsername(retrieveUserIdReq.getUsername())
                    .orElseThrow(()-> new UsernameNotFoundException("Username not found"));
            String userId = exisitingUser.getId();

            retrieveUserIdResponse.setCode(InitConfig.SUCCESS);
            retrieveUserIdResponse.setTitle(InitConfig.TITLE_SUCCESS);
            retrieveUserIdResponse.setMessage("User Found");
            retrieveUserIdResponse.setUserData(new UserData(userId));
            return ResponseEntity.ok(retrieveUserIdResponse);

        } else {
            log.info("User not found");

            retrieveUserIdResponse.setCode(InitConfig.USERNAME_NOT_FOUND);
            retrieveUserIdResponse.setTitle(InitConfig.TITLE_FAILED);
            retrieveUserIdResponse.setMessage("User not found");
            retrieveUserIdResponse.setUserData(new UserData(null));
            return ResponseEntity.ok(retrieveUserIdResponse);
        }
    }
}
