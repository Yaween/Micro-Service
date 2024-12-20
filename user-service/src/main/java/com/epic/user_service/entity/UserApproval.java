package com.epic.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "USER_APPROVAL_REQUEST")

public class UserApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "USER_TYPE")
    private String userType;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "APPROVED_ADMIN_ID")
    private String adminUserId;
}
