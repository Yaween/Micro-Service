package com.epic.order_service.util;


import com.epic.order_service.dto.TokenCheck;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JWTValidator {
    //todo: SECRET KEY should not be hardcoded
    private static final String SECRET_KEY = "zNjM4ErxA2dD8dc1yoEzMKPV3shhsyVqCbPJSW0hghA=";

    public TokenCheck validateToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();
        String userType = claims.get("userType", String.class); // Retrieve the role claim

        return new TokenCheck(username, userType);
    }
}
