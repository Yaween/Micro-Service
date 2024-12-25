package com.epic.user_service.dto;

import com.epic.user_service.dto.retailer.UserData;
import com.epic.user_service.entity.User;
import com.epic.user_service.entity.UserApproval;
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
    private TokenData token;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private User user;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Map<String, Object>> approvalList;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private UserData userData;
}
