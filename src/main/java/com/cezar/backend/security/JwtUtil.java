package com.cezar.backend.security;

import com.cezar.backend.entities.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, Role role) {
        return Jwts.builder()
                .subject(email)
                .claim("role",role)// ← was setSubject()
                .issuedAt(new Date())                                                 // ← was setIssuedAt()
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))  // ← was setExpiration()
                .signWith(key)                                                        // ← removed SignatureAlgorithm
                .compact();
    }


    public String getEmailFromToken(String token) {
        return Jwts.parser()                  // ← was parserBuilder()
                .verifyWith(key)              // ← was setSigningKey()
                .build()
                .parseSignedClaims(token)     // ← was parseClaimsJws()
                .getPayload()                 // ← was getBody()
                .getSubject();
    }
    public String getRoleFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()                     // ← was parserBuilder()
                    .verifyWith(key)              // ← was setSigningKey()
                    .build()
                    .parseSignedClaims(token);    // ← was parseClaimsJws()
            return true;
        } catch (SecurityException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }
}