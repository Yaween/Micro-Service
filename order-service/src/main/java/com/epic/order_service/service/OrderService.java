package com.epic.order_service.service;

import com.epic.order_service.controller.OrderController;
import com.epic.order_service.dto.AddOrderReq;
import com.epic.order_service.dto.CommonResponse;
import com.epic.order_service.repository.OrderRepository;
import com.epic.order_service.util.RequestValidator;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
//@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
//@Slf4j

public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public ResponseEntity<CommonResponse> addOrder(String authorizationHeader, AddOrderReq addOrderReq){
        CommonResponse addOrderResponse = new CommonResponse();
        RequestValidator requestValidator = new RequestValidator();

        String token = authorizationHeader.substring(7);
        String tokenValidity = requestValidator.validateReq(addOrderReq.getUsername(), token);

        if(tokenValidity.equalsIgnoreCase("VALID")){
            log.info("Token Validated");

            addOrderResponse.setCode("Code");
            addOrderResponse.setTitle("Failed");
            addOrderResponse.setMessage("Token Validated");
            return ResponseEntity.ok(addOrderResponse);

        } else {
            log.info("Token Failed");

            addOrderResponse.setCode("Code");
            addOrderResponse.setTitle("Failed");
            addOrderResponse.setMessage("Token Failure");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(addOrderResponse);
        }
    }
}
