package com.epic.order_service.controller;

import com.epic.order_service.dto.AddOrderReq;
import com.epic.order_service.dto.CommonResponse;
import com.epic.order_service.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/order")
//@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor

//@Slf4j

public class OrderController {

    @Autowired
    private OrderService orderService;

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @PostMapping("/add-order")
    public ResponseEntity<CommonResponse> addOrder(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody AddOrderReq addOrderReq){
        CommonResponse addOrderResponse = new CommonResponse();
        try{
            log.info("Add order request received");
            log.info("Token received : " + authorizationHeader);
            return orderService.addOrder(authorizationHeader, addOrderReq);

        } catch (Exception e){
            log.info("Error occurred : ", e);

            addOrderResponse.setCode("Code");
            addOrderResponse.setTitle("Failed");
            addOrderResponse.setMessage("Error occurred while sending the request");
            return ResponseEntity.badRequest().body(addOrderResponse);
        }
    }
}
