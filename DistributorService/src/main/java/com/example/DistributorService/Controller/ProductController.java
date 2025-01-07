package com.example.DistributorService.Controller;


import brave.Response;
import com.example.DistributorService.DTO.*;
import com.example.DistributorService.Service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<CommonResponse> addProduct(@RequestBody ProductDTO productDTO) {
        CommonResponse response = productService.addProduct(productDTO);
        return ResponseEntity.status(Integer.parseInt(response.getCode())).body(response);
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<CommonResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("/get-products")
    public ResponseEntity<CommonResponse> getProducts(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody GetProductsReq getProductsReq
    ){
        return productService.getProducts(authorizationHeader, getProductsReq);
    }
    @PostMapping("/process-order")
    public ResponseEntity<CommonResponse> checkAndProcessOrder(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody OrderProductRequestDTO requestDTO) {
        return productService.checkAndProcessOrder(authorizationHeader, requestDTO);
    }

    @PostMapping("/get-order-requests")
    public ResponseEntity<CommonResponse> getOrderRequests(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody GetOrdersReq getOrdersReq) {
        return productService.getOrderRequests(authorizationHeader, getOrdersReq);
    }

    @PostMapping("/availabilityCheck")
    public boolean checkAvailability(
            @RequestBody ProductAvailabilityCheckDTO productAvailabilityCheckDTO){
        return productService.checkProductAvailability(productAvailabilityCheckDTO);
    }

    @PostMapping("/request-product")
    public ResponseEntity<CommonResponse> requestProduct(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody RequestProductDTO requestProductDTO
    ){
        log.info("Product requesting request received");
        return productService.requestProduct(authorizationHeader, requestProductDTO);
    }
}