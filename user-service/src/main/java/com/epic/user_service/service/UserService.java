package com.epic.user_service.service;

import com.epic.user_service.config.InitConfig;
import com.epic.user_service.dto.*;
import com.epic.user_service.entity.User;
import com.epic.user_service.repository.UserRepository;
import com.epic.user_service.util.JWTGenerator;
import com.epic.user_service.util.PasswordUtil;
import com.epic.user_service.util.RequestNullChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

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

            registerResponse.setCode(InitConfig.MISSING_FIELDS_REGISTRATION);
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
            registerResponse.setMessage("Email is already taken.");
            return ResponseEntity.badRequest().body(registerResponse);
        }

        //checks whether the contact number is already existing
        if(userRepository.findByContactNumber(userRegisterReq.getContactNumber()).isPresent()){
            log.info("Contact Number is already taken");

            registerResponse.setCode(InitConfig.CONTACT_NO_TAKEN);
            registerResponse.setTitle(InitConfig.TITLE_FAILED);
            registerResponse.setMessage("Contact Number is already taken.");
            return ResponseEntity.badRequest().body(registerResponse);
        }

        log.info("User is validated with existing users");

        //validate the password with the policy
        String enteredPassword = userRegisterReq.getPassword();
        String message = PasswordUtil.passwordPolicyChecker(enteredPassword);

        if(message.equalsIgnoreCase("VALID")){
            log.info("User is validated to register");

            //generate a random unique string for userId
            UUID uuid = UUID.randomUUID();
            String userId = uuid.toString().replace("-", "").toUpperCase();

            //set the data to save in DB
            User user = new User();
            user.setId(userId);
            user.setUsername(userRegisterReq.getUsername());
            user.setPassword(PasswordUtil.encodeToBase64(userRegisterReq.getPassword())); //save the encoded password
            user.setFirstName(userRegisterReq.getFirstName());
            user.setLastName(userRegisterReq.getLastName());
            user.setEmail(userRegisterReq.getEmail());
            user.setContactNumber(userRegisterReq.getContactNumber());
            userRepository.save(user);
            log.info("User saved in the system successfully");

            registerResponse.setCode(InitConfig.SUCCESS);
            registerResponse.setTitle(InitConfig.TITLE_SUCCESS);
            registerResponse.setMessage("User registered successfully");
            return ResponseEntity.ok(registerResponse);

        } else {
            //password is not met with the password policy
            log.info("Password is not valid with password policy");

            registerResponse.setCode(InitConfig.PASSWORD_INVALID);
            registerResponse.setTitle(InitConfig.TITLE_FAILED);
            registerResponse.setMessage("Password is invalid due to missing" + message);
            return ResponseEntity.badRequest().body(registerResponse);
        }
    }

    /**
     * // User login method
     */
    public ResponseEntity<CommonResponse> loginUser(UserLoginReq userLoginReq){
        CommonResponse loginResponse = new CommonResponse();
        RequestNullChecker requestNullChecker = new RequestNullChecker();
        JWTGenerator jwtGenerator = new JWTGenerator();

        //validate the request body
        if(requestNullChecker.isNullOrEmpty(userLoginReq.getUsername(), userLoginReq.getPassword())){
            log.info("Login request consist of missing values");

            loginResponse.setCode(InitConfig.MISSING_FIELDS_LOGIN);
            loginResponse.setTitle(InitConfig.TITLE_FAILED);
            loginResponse.setMessage("Username or Password is missing");
            return ResponseEntity.badRequest().body(loginResponse);
        }

        //check the username availability
        if(userRepository.findByUsername(userLoginReq.getUsername()).isPresent()){
            log.info("Username found");

            //extract current password saved in database
            User existingUser = userRepository.findByUsername(userLoginReq.getUsername()).orElseThrow(null);
            String currentPassword = existingUser.getPassword();
            String enteredPassword = PasswordUtil.encodeToBase64(userLoginReq.getPassword());

            if(currentPassword.equals(enteredPassword)){
                //password match flow
                log.info("Password is correct");

                //generating JWT Token
                String token = jwtGenerator.generateToken(userLoginReq.getUsername());

                loginResponse.setCode(InitConfig.SUCCESS);
                loginResponse.setTitle(InitConfig.TITLE_SUCCESS);
                loginResponse.setMessage("Login Successful");
                loginResponse.setToken(new TokenData(token));

            } else {
                //password mismatch flow
                log.info("Entered password is incorrect");

                loginResponse.setCode(InitConfig.PASSWORD_INCORRECT);
                loginResponse.setTitle(InitConfig.TITLE_FAILED);
                loginResponse.setMessage("Password is incorrect");
            }

        } else {
            //username not found flow
            log.info("Username not found");

            loginResponse.setCode(InitConfig.INVALID_USERNAME);
            loginResponse.setTitle(InitConfig.TITLE_FAILED);
            loginResponse.setMessage("Username is not valid");
        }
        return ResponseEntity.ok(loginResponse);
    }

    public ResponseEntity<CommonResponse> changePassword(ChangePasswordReq changePasswordReq){
        CommonResponse changePWResponse = new CommonResponse();
        RequestNullChecker requestNullChecker = new RequestNullChecker();

        if(requestNullChecker.isNullOrEmpty(changePasswordReq.getUsername(),
                changePasswordReq.getOldPassword(), changePasswordReq.getNewPassword())){
            log.info("Fields are missing");

            changePWResponse.setCode(InitConfig.MISSING_FIELDS_FORGOT_PW);
            changePWResponse.setTitle(InitConfig.TITLE_FAILED);
            changePWResponse.setMessage("Change password request has empty fields");
            return ResponseEntity.badRequest().body(changePWResponse);
        }

        if(userRepository.findByUsername(changePasswordReq.getUsername()).isPresent()){
            log.info("User found");

            User existingUser = userRepository.findByUsername(changePasswordReq.getUsername()).orElseThrow(null);
            String currentPassword = existingUser.getPassword();
            String enteredPassword = PasswordUtil.encodeToBase64(changePasswordReq.getOldPassword());

            if(currentPassword.equals(enteredPassword)){
                log.info("Password is valid");

                String newPassword = changePasswordReq.getNewPassword();
                String message = PasswordUtil.passwordPolicyChecker(newPassword);

                if(message.equalsIgnoreCase("VALID")){
                    log.info("New password is valid");
                    existingUser.setPassword(PasswordUtil.encodeToBase64(newPassword));
                    userRepository.save(existingUser);
                    log.info("User saved successfully");

                    changePWResponse.setCode(InitConfig.SUCCESS);
                    changePWResponse.setTitle(InitConfig.TITLE_SUCCESS);
                    changePWResponse.setMessage("Password changed successfully");

                } else {
                    log.info("New Password does not follow password policy");

                    changePWResponse.setCode(InitConfig.NEW_PASSWORD_INVALID);
                    changePWResponse.setTitle(InitConfig.TITLE_FAILED);
                    changePWResponse.setMessage("New password is not valid :" + message);
                }
            } else {
                log.info("Old Password mismatch");

                changePWResponse.setCode(InitConfig.OLD_PASSWORD_MISMATCH);
                changePWResponse.setTitle(InitConfig.TITLE_FAILED);
                changePWResponse.setMessage("Old password is incorrect");
            }
        } else {
            log.info("User is not found with the give username");

            changePWResponse.setCode(InitConfig.USERNAME_INVALID);
            changePWResponse.setTitle(InitConfig.TITLE_FAILED);
            changePWResponse.setMessage("Username is not found");
        }
        return ResponseEntity.ok(changePWResponse);
    }
}
