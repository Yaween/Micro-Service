package com.epic.user_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "USER")
public class User {

    @Id
    @Column(name = "USER_ID", unique = true, nullable = false)
    private String Id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Column(name = "CONTACT_NO", unique = true)
    private String contactNumber;

    @Column(name = "USER_TYPE")
    private String userType;

    @Column(name = "USERNAME", unique = true)
    private String username;

    @Column(name = "PASSWORD", unique = true)
    private String password;

    @Column(name = "CREATED_TIME")
    private LocalDateTime createdTime;

    @Column(name = "UPDATED_TIME")
    private LocalDateTime updatedTime;

    @Column(name = "STATUS")
    private String status;
}
