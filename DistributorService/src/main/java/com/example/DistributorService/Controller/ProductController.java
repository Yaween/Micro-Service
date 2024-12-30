package com.example.DistributorService.Controller;


import com.example.DistributorService.DTO.CommonResponse;
import com.example.DistributorService.DTO.GetOrdersReq;
import com.example.DistributorService.DTO.OrderProductRequestDTO;
import com.example.DistributorService.DTO.ProductDTO;
import com.example.DistributorService.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<CommonResponse> addProduct(@RequestBody ProductDTO productDTO) {
        CommonResponse response = productService.addProduct(productDTO);
        return ResponseEntity.status(Integer.parseInt(response.getCode())).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<CommonResponse> getAllProducts() {
        CommonResponse response = productService.getAllProducts();
        return ResponseEntity.status(Integer.parseInt(response.getCode())).body(response);
    }

    @PostMapping("/process-order")
    public ResponseEntity<CommonResponse> checkAndProcessOrder(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody OrderProductRequestDTO requestDTO) {
        CommonResponse response = productService.checkAndProcessOrder(authorizationHeader, requestDTO);
        return ResponseEntity.status(Integer.parseInt(response.getCode())).body(response);
    }

    @PostMapping("/get-order-requests")
    public ResponseEntity<CommonResponse> getOrderRequests(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody GetOrdersReq getOrdersReq) {
        CommonResponse response = productService.getOrderRequests(authorizationHeader, getOrdersReq);
        return ResponseEntity.status(Integer.parseInt(response.getCode())).body(response);
    }

}