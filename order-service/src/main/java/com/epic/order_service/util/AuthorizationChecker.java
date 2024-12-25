package com.epic.order_service.util;


import com.epic.order_service.config.InitConfig;
import com.epic.order_service.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AuthorizationChecker {

    public CommonResponse authorizationCheck(String authorizationHeader, String username, String userType){
        CommonResponse response = new CommonResponse();
        RequestValidator requestValidator = new RequestValidator();

        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.warn("Authorization header is missing or empty. Request cannot be processed.");

            response.setCode(InitConfig.TOKEN_MISSING);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("Token is missing");
            return response;
        }

        // Extract and validate the token
        String token = authorizationHeader.substring(7);
        boolean tokenValidity = requestValidator.validateReq(username, userType, token);

        if(!tokenValidity){
            log.info("Token invalid or expired");

            response.setCode(InitConfig.TOKEN_INVALID_EXPIRED);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("Token is Invalid or Expired");
            return response;
        }

        response.setCode(InitConfig.TOKEN_VALID);
        response.setTitle(InitConfig.TITLE_SUCCESS);
        response.setMessage("Token Validated");
        return response;
    }
}
