package com.onelab.users_service.config.security;

import com.onelab.users_service.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtProvider {
    final JwtProperties jwtProperties;
    Key key;

    @PostConstruct
    void init() {
        key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String generateAccessToken(UserDetails userDetails) {
        Claims claims = Jwts.claims();
        claims.setSubject(userDetails.getUsername());
        claims.put("permissions", userDetails.getAuthorities());
        claims.setIssuer("Smart-tailor-app");
        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(new Date().getTime() + jwtProperties.getAccess()));
        return Jwts.builder()
                .setClaims(claims)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Claims claims = Jwts.claims();
        claims.setSubject(userDetails.getUsername());
        claims.setIssuer("Smart-tailor-app");
        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(new Date().getTime() + jwtProperties.getRefresh()));
        return Jwts.builder()
                .setClaims(claims)
                .signWith(key)
                .compact();
    }
    public boolean validateToken(String token) {
        Claims claims = parseClaims(token);
        return !claims.getExpiration().before(new Date()) && claims.getIssuer().equals("Smart-tailor-app");
    }
    private Claims parseClaims(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

    public String getEmail(String token) {
        return parseClaims(token).getSubject();
    }
}
