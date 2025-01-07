package com.example.DistributorService.client;

import com.example.DistributorService.DTO.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("order-service")
public interface OrderServiceClient {

    @PostMapping("/api/order/get-order-list")
    ResponseEntity<CommonResponse> getOrders(@RequestBody SendOrderListRetrieve sendOrderListRetrieve);

    @PostMapping("/api/order/retrieve-order-info")
    ResponseEntity<CommonResponse> retrieveOrderInfo(@RequestBody RetrieveOrderReq retrieveOrderReq);

    @PostMapping("/api/order/update-order")
    ResponseEntity<CommonResponse> updateOrder(@RequestBody UpdateOrderReq updateOrderReq);

    @PostMapping("/api/order/check-req-status")
    String checkOrderReqStatus(@RequestBody CheckOrderReqStatus checkOrderReqStatus);
}
