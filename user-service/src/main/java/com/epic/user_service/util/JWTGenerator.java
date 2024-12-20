package com.epic.user_service.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JWTGenerator {
    private static final String SECRET_KEY = "zNjM4ErxA2dD8dc1yoEzMKPV3shhsyVqCbPJSW0hghA=";
    private static final long EXPIRATION_TIME = 86400000; // 1 day

    public String generateToken(String username, String userType) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userType", userType) // Add the role as a custom claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
