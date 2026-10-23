package com.example.onlineshopback.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.example.onlineshopback.model.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

     // تولید توکن با اطلاعات کاربر
     public String generateToken(Users user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("userId", user.getUserId());
        claims.put("email", user.getEmail());

        return createToken(claims, user.getEmail());
     }
    
     //final method for generate token
     private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

     }
     private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    //get data from token
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }
    //get Email(subject)
    public String extractUsername(String token){ 
        return extractClaim(token, Claims::getSubject);
    }

    //get especial token from claim
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //get expiration token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String tokrn) {
        try {
            return extractExpiration(tokrn).before(new Date());
        }catch(ExpiredJwtException ex){
            return true;
        }
    }

    //chekc to validate token 
    public boolean validateToken(String token, String email ) {
        final String username = extractUsername(token);
        return(username.equals(email) && !isTokenExpired(token));
    }

    //Checking whether the token is fully valid or not
    public boolean isValidAndNotExpired(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        }catch (Exception e) {
            return false;
        }
    }
    //get role from token 
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return (String)claims.get("role");
    }

    //get user id from toke 
    public Integer extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return (Integer)claims.get("userId");
    }


}
