package com.epic.order_service.util;

import com.epic.order_service.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Slf4j
public class RequestValidator {
    private static final Logger log = LoggerFactory.getLogger(RequestValidator.class);

    public String validateReq(String username, String token){
        JWTValidator jwtValidator = new JWTValidator();
        String tokenUsername = jwtValidator.validateToken(token);

        if(username.equals(tokenUsername)){
            log.info("Token validated");
            log.info(tokenUsername);
            return "VALID";
        } else {
            log.info("Token validation failed");
            log.info(tokenUsername);
            return "INVALID";
        }
    }
}
