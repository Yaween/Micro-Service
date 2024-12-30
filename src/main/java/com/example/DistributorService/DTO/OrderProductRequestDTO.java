package com.example.DistributorService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductRequestDTO {
    private String username;
    private String distributorId;
    private Long productId;
    private Integer productQuantity;

}
