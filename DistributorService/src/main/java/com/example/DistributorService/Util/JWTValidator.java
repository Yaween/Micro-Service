package com.example.DistributorService.Util;

import com.example.DistributorService.DTO.TokenData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;


public class JWTValidator {
    private static final String SECRET_KEY = "zNjM4ErxA2dD8dc1yoEzMKPV3shhsyVqCbPJSW0hghA=";

    public TokenData validateToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();
        String userType = claims.get("userType", String.class); // Retrieve the role claim

        return new TokenData(username, userType);
    }
}
