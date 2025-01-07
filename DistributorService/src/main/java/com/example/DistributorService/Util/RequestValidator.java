package com.example.DistributorService.Util;

import com.example.DistributorService.DTO.TokenData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestValidator {
    public boolean validateReq(String username, String role, String token) {
        JWTValidator jwtValidator = new JWTValidator();
        TokenData tokendata = jwtValidator.validateToken(token);

        if (username.equals(tokendata.getUsername()) && role.equals(tokendata.getRole())) {
            log.info("Token validated");
            log.info("{} {}", tokendata.getUsername(), tokendata.getRole());
            return true;
        } else {
            log.info("Token validation failed");
            log.info("{} {}", tokendata.getUsername(), tokendata.getRole());
            return false;
        }
    }
}


