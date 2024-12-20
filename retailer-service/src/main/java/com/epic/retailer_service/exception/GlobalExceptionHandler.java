package com.epic.retailer_service.exception;

import com.epic.retailer_service.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DistributorReqNotFoundException.class)
    public ResponseEntity<CommonResponse> handleDistributorReqNotFoundException(DistributorReqNotFoundException ex) {
        CommonResponse response = new CommonResponse();
        log.info("Exception Occurred", ex);

        response.setCode("Code");
        response.setTitle("Failed");
        response.setMessage("Request not found");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RetailerNotFoundException.class)
    public ResponseEntity<CommonResponse> handleRetailerNotFoundException(RetailerNotFoundException ex) {
        CommonResponse response = new CommonResponse();
        log.info("Exception Occurred", ex);

        response.setCode("Code");
        response.setTitle("Failed");
        response.setMessage("Retailer not found");
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
