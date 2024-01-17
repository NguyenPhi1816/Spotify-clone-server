package com.khaphi.spotifycloneserver.service;

import com.khaphi.spotifycloneserver.config.RsaKeyProperties;
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

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(calculateExpirationTime()))
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

    private long calculateExpirationTime() {
        String expirationTimeStr = environment.getProperty("expiration_time");

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
