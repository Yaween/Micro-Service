package com.example.DistributorService.Service;

import brave.Response;
import com.example.DistributorService.Configuration.InitConfig;
import com.example.DistributorService.DTO.*;
import com.example.DistributorService.Entity.Distributor;
import com.example.DistributorService.Entity.DistributorProductMapper;
import com.example.DistributorService.Entity.Product;
import com.example.DistributorService.Repository.DistributorProductMapperRepository;
import com.example.DistributorService.Repository.DistributorRepository;
import com.example.DistributorService.Repository.ProductRepository;
import com.example.DistributorService.Util.RequestValidator;
import com.example.DistributorService.client.OrderServiceClient;
import com.example.DistributorService.client.UserServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DistributorProductMapperRepository distributorProductMapperRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private OrderServiceClient orderServiceClient;


    public CommonResponse addProduct(ProductDTO productDTO) {
        CommonResponse response = new CommonResponse();

        // Check if the product already exists by name
        if (productRepository.existsByProductName(productDTO.getProductName())) {
            response.setCode("400");
            response.setTitle("Failed");
            response.setMessage("Product with the same name already exists: " + productDTO.getProductName());
        } else {
            // Map ProductDTO to Product entity
            Product product = new Product();
            product.setProductName(productDTO.getProductName());
            product.setProductDescription(productDTO.getProductDescription());

            // Save the new product
            productRepository.save(product);
            response.setCode("200");
            response.setTitle("Success");
            response.setMessage("Product added successfully.");
        }

        return response;
    }

    // Get all products
    public ResponseEntity<CommonResponse> getAllProducts() {
        CommonResponse response = new CommonResponse();

        // Fetch all products from the repository
        List<ProductDTO> productList = productRepository.findAll().stream().map(product -> {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(product.getId());
            productDTO.setProductName(product.getProductName());
            productDTO.setProductDescription(product.getProductDescription());
            return productDTO;
        }).collect(Collectors.toList());

        if (productList.isEmpty()) {
            response.setCode(InitConfig.PRODUCT_NOT_FOUND);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("No products are available in the system.");
        } else {
            response.setCode(InitConfig.SUCCESS);
            response.setTitle(InitConfig.TITLE_SUCCESS);
            response.setMessage("Products retrieved successfully.");
            response.setProductList(productList); // Add the list of products to the response
        }

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<CommonResponse> checkAndProcessOrder(String authorizationHeader, OrderProductRequestDTO requestDTO) {
        CommonResponse response = new CommonResponse();
        RequestValidator requestValidator = new RequestValidator();

        // Validate token
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.warn("Authorization header is missing");
            response.setCode(InitConfig.TOKEN_MISSING);
            response.setTitle(InitConfig.TITLE_UNAUTHORIZED);
            response.setMessage("Token is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = authorizationHeader.substring(7); // Extract token
        boolean isValid = requestValidator.validateReq(requestDTO.getUsername(), "DISTRIBUTOR", token);

        if (!isValid) {
            log.warn("Token validation failed");
            response.setCode(InitConfig.TOKEN_INVALID_EXPIRED);
            response.setTitle(InitConfig.TITLE_UNAUTHORIZED);
            response.setMessage("Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        CheckOrderReqStatus checkOrderReqStatus = new CheckOrderReqStatus(requestDTO.getOrderId());
        String reqValidity = orderServiceClient.checkOrderReqStatus(checkOrderReqStatus);

        if(reqValidity.equalsIgnoreCase("ALTERED")) {
            log.info("Request already altered");

            response.setCode(InitConfig.REQUEST_ALREADY_ALTERED);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("Request has already being altered");
            return ResponseEntity.badRequest().body(response);

        } else if (reqValidity.equalsIgnoreCase("ABSENT")) {
            log.info("Request not found");

            response.setCode(InitConfig.REQUEST_NOT_FOUND);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("The order request cannot be found");
            return ResponseEntity.badRequest().body(response);
        }

        if (requestDTO.getOption().equalsIgnoreCase("REJECT")) {
            log.info("Req Rejected");

            UpdateOrderReq updateOrderReq = new UpdateOrderReq(requestDTO.getOrderId(), "REJECT");
            String code = orderServiceClient.updateOrder(updateOrderReq).getBody().getCode();

            if (code.equalsIgnoreCase("0000")) {
                response.setCode(InitConfig.SUCCESS);
                response.setTitle(InitConfig.TITLE_SUCCESS);
                response.setMessage("Request was rejected successfully");
                return ResponseEntity.ok(response);

            } else {
                log.info("Unsuccessful Response");

                response.setCode(InitConfig.UNSUCCESSFUL_RESPONSE);
                response.setTitle(InitConfig.TITLE_FAILED);
                response.setMessage("Unsuccessful Response");
                return ResponseEntity.badRequest().body(response);
            }
        }

        if (!requestDTO.getOption().equalsIgnoreCase("APPROVE")){
            log.info("Option is unidentified");

            response.setCode(InitConfig.UNIDENTIFIED_OPTION);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("Invalid Option");
            return ResponseEntity.badRequest().body(response);
        }

        SendUserIdRetrieve sendUserIdRetrieve = new SendUserIdRetrieve(requestDTO.getUsername());
        String userId = userServiceClient.retrieveUserId(sendUserIdRetrieve).getBody().getUserData().getUserId();

        if (userId == null || userId.isEmpty()) {
            log.info("Username is invalid");

            response.setCode(InitConfig.USERNAME_NOT_FOUND);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("Username not found in the system");
            return ResponseEntity.badRequest().body(response);
        }

        Distributor exisitngDistributor = distributorRepository.findByUserId(userId)
                .orElseThrow(null);
        String distributorId = exisitngDistributor.getId();
        log.info("Distributor Id retrieved : {}", distributorId);

        RetrieveOrderReq retrieveOrderReq = new RetrieveOrderReq(requestDTO.getOrderId());
        OrderData orderData = orderServiceClient.retrieveOrderInfo(retrieveOrderReq).getBody().getOrderData();

        if (orderData == null) {
            log.info("Order Data is null");

            response.setCode(InitConfig.UNSUCCESSFUL_RESPONSE);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("Order Data is not retrieved successfully");
            return ResponseEntity.badRequest().body(response);
        }

        String productId = orderData.getProductId();
        Integer productCount = orderData.getProductCount();

        String productTableKey = distributorId + productId;

        if (distributorProductMapperRepository.findById(productTableKey).isPresent()) {
            DistributorProductMapper distributorProductMapper = distributorProductMapperRepository.findById(productTableKey)
                    .orElseThrow(null);
            Integer availableProductCount = distributorProductMapper.getProductQuantity();

            if (availableProductCount > productCount) {
                log.info("Stocks available");

                UpdateOrderReq updateOrderReq = new UpdateOrderReq();
                updateOrderReq.setOrderId(requestDTO.getOrderId());
                updateOrderReq.setOption("APPROVE");

                String code = orderServiceClient.updateOrder(updateOrderReq).getBody().getCode();

                if (code.equalsIgnoreCase("0000")) {
                    distributorProductMapper.setProductQuantity(distributorProductMapper.getProductQuantity() -
                            productCount);
                    distributorProductMapperRepository.save(distributorProductMapper);

                    response.setCode(InitConfig.SUCCESS);
                    response.setTitle(InitConfig.TITLE_SUCCESS);
                    response.setMessage("The order was approved and informed to the retailer successfully");
                    return ResponseEntity.ok(response);

                } else {
                    log.info("Unsuccessful Response");

                    response.setCode(InitConfig.UNSUCCESSFUL_RESPONSE);
                    response.setTitle(InitConfig.TITLE_FAILED);
                    response.setMessage("Unsuccessful Response");
                    return ResponseEntity.badRequest().body(response);
                }

            } else {
                log.info("Stocks not enough");

                UpdateOrderReq updateOrderReq = new UpdateOrderReq(retrieveOrderReq.getOrderId(), "REJECT");
                String codeNew = orderServiceClient.updateOrder(updateOrderReq).getBody().getCode();

                if (codeNew.equalsIgnoreCase("0000")) {

                    response.setCode(InitConfig.INSUFFICIENT_STOCKS);
                    response.setTitle(InitConfig.TITLE_FAILED);
                    response.setMessage("The order was rejected due to the insufficient stocks");
                    return ResponseEntity.ok(response);

                } else {
                    log.info("Unsuccessful Response");

                    response.setCode(InitConfig.UNSUCCESSFUL_RESPONSE);
                    response.setTitle(InitConfig.TITLE_FAILED);
                    response.setMessage("Unsuccessful Response");
                    return ResponseEntity.badRequest().body(response);
                }
            }

        } else {
            log.info("Product-Distributor connection not found");

            UpdateOrderReq updateOrderReq = new UpdateOrderReq(requestDTO.getOrderId(), "REJECT");
            String code = orderServiceClient.updateOrder(updateOrderReq).getBody().getCode();

            if (code.equalsIgnoreCase("0000")) {

                response.setCode(InitConfig.PRODUCT_NOT_ADDED_TO_ACCOUNT);
                response.setTitle(InitConfig.TITLE_FAILED);
                response.setMessage("The product is not added to your account");
                return ResponseEntity.badRequest().body(response);

            } else {
                log.info("Unsuccessful Response");

                response.setCode(InitConfig.UNSUCCESSFUL_RESPONSE);
                response.setTitle(InitConfig.TITLE_FAILED);
                response.setMessage("Unsuccessful Response");
                return ResponseEntity.badRequest().body(response);
            }
        }
    }

    public ResponseEntity<CommonResponse> getOrderRequests(String authorizationHeader, GetOrdersReq getOrdersReq) {
        CommonResponse response = new CommonResponse();
        RequestValidator requestValidator = new RequestValidator();

        // Validate token
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.warn("Authorization header is missing");
            response.setCode(InitConfig.TOKEN_MISSING);
            response.setTitle(InitConfig.TITLE_UNAUTHORIZED);
            response.setMessage("Token is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = authorizationHeader.substring(7); // Extract token
        boolean isValid = requestValidator.validateReq(getOrdersReq.getUsername(), "DISTRIBUTOR", token);

        if (!isValid) {
            log.warn("Token validation failed");
            response.setCode(InitConfig.TOKEN_INVALID_EXPIRED);
            response.setTitle(InitConfig.TITLE_UNAUTHORIZED);
            response.setMessage("Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        SendUserIdRetrieve sendUserIdRetrieve = new SendUserIdRetrieve(getOrdersReq.getUsername());
        String userId = userServiceClient.retrieveUserId(sendUserIdRetrieve).getBody().getUserData().getUserId();

        if (userId == null || userId.isEmpty()) {
            log.info("User ID missing");

            response.setCode(InitConfig.USERNAME_NOT_FOUND);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("Username is invalid");
            return ResponseEntity.badRequest().body(response);
        }

        Distributor existingDistributor = distributorRepository.findByUserId(userId)
                .orElseThrow(null);
        String distributorId = existingDistributor.getId();

        SendOrderListRetrieve sendOrderListRetrieve = new SendOrderListRetrieve();
        sendOrderListRetrieve.setUsername(getOrdersReq.getUsername());
        sendOrderListRetrieve.setDistributorId(distributorId);

        List<Map<String, Object>> filteredOrderList = orderServiceClient.getOrders(sendOrderListRetrieve).getBody().getOrderList();

        if(filteredOrderList == null || filteredOrderList.isEmpty()){
            log.info("List is empty");

            response.setCode(InitConfig.SUCCESS);
            response.setTitle(InitConfig.TITLE_SUCCESS);
            response.setMessage("No any pending order requests");

        }else {

            response.setCode(InitConfig.SUCCESS);
            response.setTitle(InitConfig.TITLE_SUCCESS);
            response.setMessage("Pending order list retrieved");
            response.setOrderList(filteredOrderList);
        }
        return ResponseEntity.ok(response);
    }

    public boolean checkProductAvailability(ProductAvailabilityCheckDTO productAvailabilityCheckDTO) {
        //todo: Modify this method
        if(productRepository.findById(productAvailabilityCheckDTO.getProductId()).isPresent()){
            log.info("Product is available");
            return true;
        } else {
            log.info("Product Not Found");
            return false;
        }
    }

    public ResponseEntity<CommonResponse> requestProduct(String authorizationHeader, RequestProductDTO requestProductDTO) {
        CommonResponse response = new CommonResponse();
        RequestValidator requestValidator = new RequestValidator();

        // Validate token
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.warn("Authorization header is missing");
            response.setCode(InitConfig.TOKEN_MISSING);
            response.setTitle(InitConfig.TITLE_UNAUTHORIZED);
            response.setMessage("Token is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = authorizationHeader.substring(7); // Extract token
        boolean isValid = requestValidator.validateReq(requestProductDTO.getUsername(), "DISTRIBUTOR", token);

        if (!isValid) {
            log.warn("Token validation failed");
            response.setCode(InitConfig.TOKEN_INVALID_EXPIRED);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if(productRepository.findById(requestProductDTO.getProductId()).isPresent()){
            log.info("Product is available");

            Product existingProduct = productRepository.findById(requestProductDTO.getProductId())
                    .orElse(null);
            String productId = existingProduct.getId();

            SendUserIdRetrieve sendUserIdRetrieve = new SendUserIdRetrieve(requestProductDTO.getUsername());
            String userId = userServiceClient.retrieveUserId(sendUserIdRetrieve).getBody().getUserData().getUserId();

            if(userId == null || userId.isEmpty()){
                log.info("User ID missing");

                response.setCode(InitConfig.USERNAME_INVALID);
                response.setTitle(InitConfig.TITLE_FAILED);
                response.setMessage("Username is invalid");
                return ResponseEntity.badRequest().body(response);
            }

            Distributor distributor = distributorRepository.findByUserId(userId).orElseThrow(null);
            String distributorId = distributor.getId();

            DistributorProductMapper distributorProductMapper = new DistributorProductMapper();
            distributorProductMapper.setId(distributorId + productId);
            distributorProductMapper.setProductId(productId);
            distributorProductMapper.setDistributorId(distributorId);
            distributorProductMapper.setProductQuantity(requestProductDTO.getProductQuantity());
            distributorProductMapper.setPrice(requestProductDTO.getProductPrice());
            distributorProductMapperRepository.save(distributorProductMapper);

            response.setCode(InitConfig.SUCCESS);
            response.setTitle(InitConfig.TITLE_SUCCESS);
            response.setMessage("Product Successfully Added to the system");
            return ResponseEntity.ok(response);

        } else {
            log.info("Product Not Found");

            response.setCode(InitConfig.PRODUCT_NOT_FOUND);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("Product Not Found");
            return ResponseEntity.badRequest().body(response);
        }
    }

    public ResponseEntity<CommonResponse> getProducts(String authorizationHeader, GetProductsReq getProductsReq) {
        CommonResponse response = new CommonResponse();
        RequestValidator requestValidator = new RequestValidator();

        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.warn("Authorization header is missing or empty. Request cannot be processed.");

            response.setCode(InitConfig.TOKEN_MISSING);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("Token is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Extract and validate the token
        String token = authorizationHeader.substring(7);
        boolean tokenValidity = requestValidator.validateReq(getProductsReq.getUsername(), "DISTRIBUTOR", token);

        if(!tokenValidity){
            log.info("Token invalid or expired");

            response.setCode(InitConfig.TOKEN_INVALID_EXPIRED);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("Token is Invalid or Expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        List<ProductDTO> productList = productRepository.findAll().stream().map(product -> {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(product.getId());
            productDTO.setProductName(product.getProductName());
            productDTO.setProductDescription(product.getProductDescription());
            return productDTO;
        }).toList();

        if (productList.isEmpty()) {
            response.setCode(InitConfig.PRODUCT_NOT_FOUND);
            response.setTitle(InitConfig.TITLE_FAILED);
            response.setMessage("No products are available in the system.");
        } else {
            response.setCode(InitConfig.SUCCESS);
            response.setTitle(InitConfig.TITLE_SUCCESS);
            response.setMessage("Products retrieved successfully.");
            response.setProductList(productList);
        }

        return ResponseEntity.ok(response);
    }
}
