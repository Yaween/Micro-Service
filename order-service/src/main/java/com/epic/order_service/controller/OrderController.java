package com.epic.order_service.controller;

import com.epic.order_service.dto.*;
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

    //retailer calling
    @PostMapping("/receive-order")
    public ResponseEntity<CommonResponse> receiveOrder(
            @RequestBody OrderReceiveReq orderReceiveReq){
        log.info("Received the order receive request");
        return orderService.receiveOrder(orderReceiveReq);
    }

    //distributor calling
    @PostMapping("/get-order-list")
    public ResponseEntity<CommonResponse> getOrders(
            @RequestBody GetOrdersReq getOrdersReq){
        log.info("Received the order list request");
        return orderService.getOrders(getOrdersReq);
    }

    //distributor calling
    @PostMapping("/update-order")
    public ResponseEntity<CommonResponse> updateOrder( @RequestBody UpdateOrderReq updateOrderReq){
        log.info("Received update order request");
        return orderService.updateOrder(updateOrderReq);
    }

    @PostMapping("/retrieve-order-info")
    public ResponseEntity<CommonResponse> retrieveOrderInfo(
            @RequestBody RetrieveOrderReq retrieveOrderReq) {
        return orderService.retrieveOrderInfo(retrieveOrderReq);
    }

    @PostMapping("/check-req-status")
    public String checkOrderReqStatus(
            @RequestBody CheckOrderReqStatus checkOrderReqStatus
    ){
        return orderService.checkOrderReqStatus(checkOrderReqStatus);
    }
}
