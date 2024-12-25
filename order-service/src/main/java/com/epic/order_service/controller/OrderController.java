package com.epic.order_service.controller;

import com.epic.order_service.dto.CommonResponse;
import com.epic.order_service.dto.GetOrdersReq;
import com.epic.order_service.dto.OrderReceiveReq;
import com.epic.order_service.dto.UpdateOrderReq;
import com.epic.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/receive-order")
    public ResponseEntity<CommonResponse> receiveOrder(@RequestBody OrderReceiveReq orderReceiveReq){
        log.info("Received the order receive request");
        return orderService.receiveOrder(orderReceiveReq);
    }

    @PostMapping("/get-order-list")
    public ResponseEntity<CommonResponse> getOrders(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody GetOrdersReq getOrdersReq){
        log.info("Received the order list request");
        return orderService.getOrders(authorizationHeader, getOrdersReq);
    }

    @PostMapping("/update-order")
    public ResponseEntity<CommonResponse> updateOrder(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody UpdateOrderReq updateOrderReq
            ){
        log.info("Received update order request");
        return orderService.updateOrder(authorizationHeader, updateOrderReq);
    }
}
