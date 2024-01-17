package com.khaphi.spotifycloneserver.service;

import com.khaphi.spotifycloneserver.config.RsaKeyProperties;
import com.khaphi.spotifycloneserver.enums.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final Environment environment;
    private final RsaKeyProperties rsaKeys;
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private String getExpiration(TokenType type) {
        String expirationTimeStr = "";
        switch (type){
            case ACCESS -> {
                expirationTimeStr = environment.getProperty("access_expiration");
                break;
            }
            case REFRESH -> {
                expirationTimeStr = environment.getProperty("refresh_expiration");
                break;
            }
            default -> {
                break;
            }
        }
        return expirationTimeStr;
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, TokenType.ACCESS);
    }

    public String generateRefeshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, TokenType.REFRESH);
    }

    private String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            TokenType type
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(calculateExpirationTime(getExpiration(type))))
                .signWith(rsaKeys.privateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private long calculateExpirationTime(String expirationTimeStr) {
        if (expirationTimeStr != null) {
            try {
                return System.currentTimeMillis() + Long.parseLong(expirationTimeStr);
            } catch (NumberFormatException e) {
                throw new IllegalStateException("expiration_time property is not a valid number", e);
            }
        } else {
            // If expiration_time is null, return current time + 24 hours (in milliseconds)
            return System.currentTimeMillis() + 24 * 60 * 60 * 1000;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(rsaKeys.publicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
