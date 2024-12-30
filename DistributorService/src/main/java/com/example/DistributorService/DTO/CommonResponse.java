package com.example.DistributorService.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse {
    private String code;
    private String title;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Object data;

    public void setData(List<Map<String, Object>> pendingRequests) {
    }

    public void setData(Object data) {
        this.data = data;
    }

    private List<ProductDTO> productList;
    private List<Map<String, Object>> orderRequestList;
}

