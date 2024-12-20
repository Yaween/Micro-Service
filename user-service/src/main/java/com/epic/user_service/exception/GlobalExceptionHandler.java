package com.epic.user_service.exception;

import com.epic.user_service.config.InitConfig;
import com.epic.user_service.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<CommonResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        CommonResponse response = new CommonResponse();
        log.info("Exception Occurred", ex);

        response.setCode(InitConfig.USERNAME_NOT_FOUND);
        response.setTitle(InitConfig.TITLE_FAILED);
        response.setMessage("Username not found");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> handleGenericException(Exception ex) {
        CommonResponse response = new CommonResponse();
        log.info("Exception Occurred", ex);

        response.setCode("Code");
        response.setTitle("Failed");
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
