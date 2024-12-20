package com.epic.order_service.service;

import com.epic.order_service.dto.CommonResponse;
import com.epic.order_service.dto.GetOrdersReq;
import com.epic.order_service.dto.OrderReceiveReq;
import com.epic.order_service.entity.Order;
import com.epic.order_service.repository.OrderRepository;
import com.epic.order_service.util.UniqueIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;

    public ResponseEntity<CommonResponse> receiveOrder(OrderReceiveReq orderReceiveReq){
        CommonResponse receiveOrderResponse = new CommonResponse();

        //todo: Validate product availability in product table

        Order order = new Order();
        order.setId(UniqueIdGenerator.generateUniqueId());
        order.setRetailerOrderId(orderReceiveReq.getOrderId());
        order.setRetailerId(orderReceiveReq.getRetailerId());
        order.setDistributorId(orderReceiveReq.getDistributorId());
        order.setProductId(orderReceiveReq.getProductId());
        order.setProductCount(orderReceiveReq.getProductCount());
        order.setStatus("PENDING");
        order.setCreatedTime(LocalDateTime.now());
        orderRepository.save(order);
        log.info("Order Request saved successfully");

        receiveOrderResponse.setCode("0000");
        receiveOrderResponse.setTitle("Success");
        receiveOrderResponse.setMessage("Order Request was saved successfully");
        return ResponseEntity.ok(receiveOrderResponse);
    }

    public ResponseEntity<CommonResponse> getOrders(GetOrdersReq getOrdersReq){
        return null;
    }
}
