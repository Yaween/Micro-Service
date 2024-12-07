package com.epic.user_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.Token;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse {
    private String code;
    private String title;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private TokenData token;
}
