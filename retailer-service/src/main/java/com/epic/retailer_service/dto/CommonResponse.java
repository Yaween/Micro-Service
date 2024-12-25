package com.epic.retailer_service.dto;

import com.epic.retailer_service.entity.AddDistributorReq;
import com.epic.retailer_service.entity.OrderRequest;
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
    private UserData userData;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Map<String, Object>> reqList;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<DistributorData> distributorList;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ProductData> productList;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Map<String, Object>> orderReqList;
}
