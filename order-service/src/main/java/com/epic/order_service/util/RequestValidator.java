package com.epic.order_service.util;

import com.epic.order_service.dto.TokenCheck;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestValidator {

    public boolean validateReq(String username, String userType, String token){
        JWTValidator jwtValidator = new JWTValidator();
        try{
            TokenCheck tokenData = jwtValidator.validateToken(token);

            if(username.equals(tokenData.getUsername()) && userType.equals(tokenData.getUserType())){
                log.info("Token validated");
                log.info("{} {}", tokenData.getUsername(), tokenData.getUserType());
                return true;
            } else {
                log.info("Token validation failed");
                log.info("{} {}", tokenData.getUsername(), tokenData.getUserType());
                return false;
            }
        } catch (Exception e){
            log.info("Error occurred while validating");

            return false;
        }

    }
}
