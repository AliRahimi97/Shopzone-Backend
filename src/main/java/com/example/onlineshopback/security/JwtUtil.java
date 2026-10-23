package com.example.onlineshopback.security;


import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.onlineshopback.model.Users;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Users users) {
        return Jwts.builder()
                .setSubject(users.getEmail())
                .claim("role", users.getRole().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // public boolean validateToken(String token) {
    //     try {
    //         Jwts.parserBuilder()
    //             .setSigningKey(getSigningKey())
    //             .build()
    //             .parseClaimsJws(token);
    //         return true;
    //     } catch (JwtException ex) {
    //         return false;
    //     }
    // }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String usernameFromToken = getEmailFromToken(token);
            return usernameFromToken.equals(userDetails.getUsername()) && !isTokenExpired(token);
        }catch (JwtException exception) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                          .setSigningKey(getSigningKey())
                          .build()
                          .parseClaimsJws(token)
                          .getBody()
                          .getExpiration();
    return expiration.before(new Date());

    }
}
