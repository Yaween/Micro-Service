package com.epic.user_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "USER_DETALS")
public class User {
    @Id
    @Column(name = "USER_ID", unique = true, nullable = false)
    private String id;

    @Column(name = "USERNAME", unique = true)
    private String username;

    @Column(name = "PASSWORD", unique = true)
    private String password;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Column(name = "CONTACT_NO", unique = true)
    private String contactNumber;

//    @Column(name = "TOKEN")
//    private String token;
}
