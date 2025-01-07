package com.epic.order_service.service;

import com.epic.order_service.client.DistributorServiceClient;
import com.epic.order_service.client.RetailerServiceClient;
import com.epic.order_service.config.InitConfig;
import com.epic.order_service.dto.*;
import com.epic.order_service.entity.Order;
import com.epic.order_service.repository.OrderRepository;
import com.epic.order_service.util.AuthorizationChecker;
import com.epic.order_service.util.RequestValidator;
import com.epic.order_service.util.UniqueIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final DistributorServiceClient distributorServiceClient;
    private final RetailerServiceClient retailerServiceClient;

    public ResponseEntity<CommonResponse> receiveOrder(OrderReceiveReq orderReceiveReq){
        CommonResponse receiveOrderResponse = new CommonResponse();

        ProductAvailabilityCheckDTO productAvailabilityCheck = new ProductAvailabilityCheckDTO(orderReceiveReq.getProductId());
        //checking product availability
        boolean productAvailability = distributorServiceClient.productAvailability(productAvailabilityCheck);

        if(!productAvailability){
            log.info("Product with given product is not available.");

            receiveOrderResponse.setCode(InitConfig.PRODUCT_ID_NOT_FOUND);
            receiveOrderResponse.setTitle(InitConfig.TITLE_FAILED);
            receiveOrderResponse.setMessage("Product with given product is not available.");
            return ResponseEntity.ok(receiveOrderResponse);
        }

        //saving order in DB
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

        receiveOrderResponse.setCode(InitConfig.SUCCESS);
        receiveOrderResponse.setTitle(InitConfig.TITLE_SUCCESS);
        receiveOrderResponse.setMessage("Order Request was saved successfully");
        return ResponseEntity.ok(receiveOrderResponse);
    }

    public ResponseEntity<CommonResponse> getOrders(GetOrdersReq getOrdersReq){
        CommonResponse getOrdersResponse = new CommonResponse();

        List<Order> orderList = orderRepository.findByDistributorId(getOrdersReq.getDistributorId());

        List<Map<String, Object>> filteredOrderList = new ArrayList<>();
        for (Order order : orderList) {
            Map<String, Object> map = new HashMap<>();
            if(order.getStatus().equalsIgnoreCase("PENDING")){
                map.put("id", order.getId());
                map.put("retailerId", order.getRetailerId());
                map.put("productId", order.getProductId());
                map.put("productCount", order.getProductCount());
                filteredOrderList.add(map);
            }
        }

        if (filteredOrderList.isEmpty()){
            getOrdersResponse.setCode(InitConfig.SUCCESS);
            getOrdersResponse.setTitle(InitConfig.TITLE_SUCCESS);
            getOrdersResponse.setMessage("Order List is empty");
            getOrdersResponse.setOrderList(null);
            return ResponseEntity.ok(getOrdersResponse);

        } else {
            getOrdersResponse.setCode(InitConfig.SUCCESS);
            getOrdersResponse.setTitle(InitConfig.TITLE_SUCCESS);
            getOrdersResponse.setMessage("Order List Retrieved successfully");
            getOrdersResponse.setOrderList(filteredOrderList);
            return ResponseEntity.ok(getOrdersResponse);
        }
    }

    public ResponseEntity<CommonResponse> updateOrder(UpdateOrderReq updateOrderReq) {
        CommonResponse updateOrderResponse = new CommonResponse();
        AuthorizationChecker authorizationChecker = new AuthorizationChecker();

            Order order = orderRepository.findById(updateOrderReq.getOrderId()).
                    orElseThrow(null); //todo: Custom Exception should be implemented
            SendOrderReqUpdate sendOrderReqUpdate = new SendOrderReqUpdate();

            if(order.getStatus().equalsIgnoreCase("PENDING")){
                if(updateOrderReq.getOption().equalsIgnoreCase("APPROVE")){

                    sendOrderReqUpdate.setOrderId(order.getRetailerOrderId());
                    sendOrderReqUpdate.setStatus("APPROVE");
                    String code = retailerServiceClient.updateOrderReqStatus(sendOrderReqUpdate).getBody().getCode();

                    if(code.equalsIgnoreCase(InitConfig.SUCCESS)){
                        order.setStatus("APPROVED");
                        order.setUpdatedTime(LocalDateTime.now());
                        orderRepository.save(order);

                        updateOrderResponse.setCode(InitConfig.SUCCESS);
                        updateOrderResponse.setTitle(InitConfig.TITLE_SUCCESS);
                        updateOrderResponse.setMessage("Order update was successful");
                        return ResponseEntity.ok(updateOrderResponse);

                    } else {
                        log.info("Request sent to retailer service is failed");

                        updateOrderResponse.setCode(InitConfig.REQUEST_FAILED);
                        updateOrderResponse.setTitle(InitConfig.TITLE_FAILED);
                        updateOrderResponse.setMessage("Request was failed");
                        return ResponseEntity.badRequest().body(updateOrderResponse);
                    }

                } else if (updateOrderReq.getOption().equalsIgnoreCase("REJECT")) {

                    sendOrderReqUpdate.setOrderId(order.getRetailerOrderId());
                    sendOrderReqUpdate.setStatus("REJECT");
                    String code = retailerServiceClient.updateOrderReqStatus(sendOrderReqUpdate).getBody().getCode();

                    if(code.equalsIgnoreCase(InitConfig.SUCCESS)){
                        order.setStatus("REJECTED");
                        order.setUpdatedTime(LocalDateTime.now());
                        orderRepository.save(order);

                        updateOrderResponse.setCode(InitConfig.SUCCESS);
                        updateOrderResponse.setTitle(InitConfig.TITLE_SUCCESS);
                        updateOrderResponse.setMessage("Order update was successful");
                        return ResponseEntity.ok(updateOrderResponse);

                    } else {
                        log.info("Request sent to retailer service is failed");

                        updateOrderResponse.setCode(InitConfig.REQUEST_FAILED);
                        updateOrderResponse.setTitle(InitConfig.TITLE_FAILED);
                        updateOrderResponse.setMessage("Request was failed");
                        return ResponseEntity.badRequest().body(updateOrderResponse);
                    }

                } else {
                    log.info("Unidentified Option from distributor");

                    updateOrderResponse.setCode(InitConfig.UNIDENTIFIED_OPTION);
                    updateOrderResponse.setTitle(InitConfig.TITLE_FAILED);
                    updateOrderResponse.setMessage("Unidentified Option");
                    return ResponseEntity.badRequest().body(updateOrderResponse);
                }

            } else {
                log.info("Order has already changed");

                updateOrderResponse.setCode(InitConfig.REQUEST_ALREADY_ALTERED);
                updateOrderResponse.setTitle(InitConfig.TITLE_FAILED);
                updateOrderResponse.setMessage("Order Request has already altered");
                return ResponseEntity.badRequest().body(updateOrderResponse);
            }

    }

    public ResponseEntity<CommonResponse> retrieveOrderInfo(RetrieveOrderReq retrieveOrderReq) {
        CommonResponse retrieveOrderInfoResponse = new CommonResponse();

        Order existingOrder = orderRepository.findById(retrieveOrderReq.getOrderId())
                .orElseThrow(null);
        String productId = existingOrder.getProductId();
        Integer productCount = existingOrder.getProductCount();

        retrieveOrderInfoResponse.setCode(InitConfig.SUCCESS);
        retrieveOrderInfoResponse.setTitle(InitConfig.TITLE_SUCCESS);
        retrieveOrderInfoResponse.setMessage("Order info retrieved successfully");
        retrieveOrderInfoResponse.setOrderData(new OrderData(productId, productCount));
        return ResponseEntity.ok(retrieveOrderInfoResponse);
    }

    public String checkOrderReqStatus(CheckOrderReqStatus checkOrderReqStatus) {
        if(orderRepository.findById(checkOrderReqStatus.getOrderId()).isPresent()){
            Order order = orderRepository.findById(checkOrderReqStatus.getOrderId()).
                    orElseThrow(null);
            String status = order.getStatus();

            if (status.equalsIgnoreCase("PENDING")){
                log.info("Request is pending");
                return "PENDING";

            } else {
                log.info("Request is altered");
                return "ALTERED";
            }
        } else {
            return "ABSENT";
        }
    }
}
