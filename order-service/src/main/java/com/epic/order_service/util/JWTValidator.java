package com.epic.order_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JWTValidator {
    private static final String SECRET_KEY = "zNjM4ErxA2dD8dc1yoEzMKPV3shhsyVqCbPJSW0hghA=";

    public String validateToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject(); // Returns the username
    }
}
