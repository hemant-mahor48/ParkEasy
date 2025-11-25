package com.parkeasy.api_gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * Validate if token is not expired
     */
    public boolean isValid(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract all claims from token
     */
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Get username (email) from token - subject claim
     */
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Get email from token (same as username in our case)
     */
    public String getEmailFromToken(String token) {
        return getUsernameFromToken(token);
    }

    /**
     * Get user ID from token - custom claim
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        Object userIdObj = claims.get("userId");

        // Handle both Integer and Long types
        return switch (userIdObj) {
            case null -> throw new IllegalArgumentException("Token does not contain userId claim");
            case Integer i -> i.longValue();
            case Long l -> l;
            default ->
                // Try to parse as string
                    Long.parseLong(userIdObj.toString());
        };

    }

    /**
     * Get user role from token - custom claim
     */
    public String getRoleFromToken(String token) {
        Claims claims = getClaims(token);
        String role = claims.get("role", String.class);

        if (role == null || role.isEmpty()) {
            throw new IllegalArgumentException("Token does not contain role claim");
        }

        return role;
    }
}