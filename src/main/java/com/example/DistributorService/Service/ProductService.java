package com.example.DistributorService.Service;


import com.example.DistributorService.DTO.CommonResponse;
import com.example.DistributorService.DTO.GetOrdersReq;
import com.example.DistributorService.DTO.OrderProductRequestDTO;
import com.example.DistributorService.DTO.ProductDTO;
import com.example.DistributorService.Entity.DistributorProductMapper;
import com.example.DistributorService.Entity.Product;
import com.example.DistributorService.Repository.DistributorProductMapperRepository;
import com.example.DistributorService.Repository.ProductRepository;
import com.example.DistributorService.Util.RequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
            product.setPrice(productDTO.getPrice());

            // Save the new product
            productRepository.save(product);
            response.setCode("200");
            response.setTitle("Success");
            response.setMessage("Product added successfully.");
        }

        return response;
    }

    // Get all products
    public CommonResponse getAllProducts() {
        CommonResponse response = new CommonResponse();

        // Fetch all products from the repository
        List<ProductDTO> productList = productRepository.findAll().stream().map(product -> {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(String.valueOf(product.getId()));
            productDTO.setProductName(product.getProductName());
            productDTO.setProductDescription(product.getProductDescription());
            productDTO.setPrice(productDTO.getPrice());
            return productDTO;
        }).collect(Collectors.toList());

        if (productList.isEmpty()) {
            response.setCode("404");
            response.setTitle("No Products Found");
            response.setMessage("No products are available in the system.");
        } else {
            response.setCode("200");
            response.setTitle("Success");
            response.setMessage("Products retrieved successfully.");
            response.setProductList(productList); // Add the list of products to the response
        }

        return response;
    }

    public CommonResponse checkAndProcessOrder(String authorizationHeader, OrderProductRequestDTO requestDTO) {
        CommonResponse response = new CommonResponse();
        RequestValidator requestValidator = new RequestValidator();

        // Validate token
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.warn("Authorization header is missing");
            response.setCode("401");
            response.setTitle("Unauthorized");
            response.setMessage("Token is missing");
            return response;
        }

        String token = authorizationHeader.substring(7); // Extract token
        boolean isValid = requestValidator.validateReq(requestDTO.getUsername(), "DISTRIBUTOR", token);

        if (!isValid) {
            log.warn("Token validation failed");
            response.setCode("401");
            response.setTitle("Unauthorized");
            response.setMessage("Invalid or expired token");
            return response;
        }

        // Find distributor-product mapping
        Optional<DistributorProductMapper> mappingOptional =
                distributorProductMapperRepository.findByDistributorIdAndProductId(Long.valueOf(requestDTO.getDistributorId()), requestDTO.getProductId());

        if (mappingOptional.isEmpty()) {
            log.warn("Distributor ID mismatch or product not found for distributor ID: {}", requestDTO.getDistributorId());
            response.setCode("400");
            response.setTitle("Rejected");
            response.setMessage("Distributor ID mismatch or product not available");

            // Save reject option in the database
            DistributorProductMapper rejectMapping = new DistributorProductMapper();
            rejectMapping.setDistributorId(Long.valueOf(requestDTO.getDistributorId()));
            rejectMapping.setProductQuantity(requestDTO.getProductQuantity());
            rejectMapping.setOption("Rejected");
            distributorProductMapperRepository.save(rejectMapping);

            return response;
        }

        DistributorProductMapper mapping = mappingOptional.get();

        // Check product availability
        if (mapping.getProductQuantity() >= requestDTO.getProductQuantity()) {
            log.info("Order accepted for distributor ID: {}", requestDTO.getDistributorId());

            // Update the option as "Accepted"
            mapping.setOption("Accepted");
            distributorProductMapperRepository.save(mapping);

            response.setCode("200");
            response.setTitle("Accepted");
            response.setMessage("Order processed successfully");
        } else {
            log.warn("Insufficient stock for distributor ID: {}", requestDTO.getDistributorId());

            // Update the option as "Rejected"
            mapping.setOption("Rejected");
            distributorProductMapperRepository.save(mapping);

            response.setCode("400");
            response.setTitle("Rejected");
            response.setMessage("Insufficient stock");
        }

        return response;
    }

    public CommonResponse getOrderRequests(String authorizationHeader, GetOrdersReq getOrdersReq) {
        CommonResponse response = new CommonResponse();
        RequestValidator requestValidator = new RequestValidator();

        // Validate token
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            log.warn("Authorization header is missing");
            response.setCode("401");
            response.setTitle("Unauthorized");
            response.setMessage("Token is missing");
            return response;
        }

        String token = authorizationHeader.substring(7); // Extract token
        boolean isValid = requestValidator.validateReq(getOrdersReq.getUsername(), "DISTRIBUTOR", token);

        if (!isValid) {
            log.warn("Token validation failed");
            response.setCode("401");
            response.setTitle("Unauthorized");
            response.setMessage("Invalid or expired token");
            return response;
        }

        // Fetch order requests by distributor ID
        List<DistributorProductMapper> orderRequests = distributorProductMapperRepository.findByDistributorId(Long.valueOf(getOrdersReq.getDistributorId()));

        if (orderRequests.isEmpty()) {
            log.warn("No order requests found for distributor ID: {}", getOrdersReq.getDistributorId());
            response.setCode("404");
            response.setTitle("Not Found");
            response.setMessage("No order requests found");
            return response;
        }

        // Map the order requests to response structure
        List<Map<String, Object>> orderRequestList = orderRequests.stream().map(order -> {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("productId", order.getProduct().getId());
            orderMap.put("productName", order.getProduct().getProductName());
            orderMap.put("productDescription", order.getProduct().getProductDescription());
            orderMap.put("productQuantity", order.getProductQuantity());
            orderMap.put("option", order.getOption());
            return orderMap;
        }).toList();

        response.setCode("200");
        response.setTitle("Success");
        response.setMessage("Order requests retrieved successfully");
        response.setOrderRequestList(orderRequestList); // Add a getter and setter for `orderRequestList` in `CommonResponse`
        return response;
    }
}
