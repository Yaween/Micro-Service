package com.epic.user_service.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeApprovalReq {
    private String adminUsername;
    private Integer approvalReqId;
    private String statusCommand;
}
