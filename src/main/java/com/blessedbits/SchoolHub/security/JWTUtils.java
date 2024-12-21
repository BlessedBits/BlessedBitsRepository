package com.blessedbits.SchoolHub.security;

import io.jsonwebtoken.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Component
public class JWTUtils {
    public String generateAccessJWT(String username) {
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + SecurityConstants.ACCESS_TOKEN_VALIDITY);

        String token = Jwts.builder()
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expirationDate)
                .signWith(SecurityConstants.SIGNING_KEY)
                .compact();
        return token;
    }

    public String generateRefreshJWT(String username) {
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + SecurityConstants.REFRESH_TOKEN_VALIDITY);

        String token = Jwts.builder()
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expirationDate)
                .signWith(SecurityConstants.SIGNING_KEY)
                .compact();
        return token;
    }

    public String getUsernameFromJWT(String token) {
        JwtParser jwtParser = Jwts.parser().setSigningKey(SecurityConstants.SIGNING_KEY).build();
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateJWT(String token) {
        try {
            JwtParser jwtParser = Jwts.parser()
                    .setSigningKey(SecurityConstants.SIGNING_KEY)
                    .build();
            jwtParser.parseClaimsJws(token); // Validate token
            System.out.println("JWT is valid.");
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("JWT expired at: " + e.getClaims().getExpiration());
            System.err.println("Current time: " + new Date());
            throw new AuthenticationCredentialsNotFoundException("JWT is expired", e);
        } catch (JwtException e) {
            System.err.println("Invalid JWT: " + e.getMessage());
            throw new AuthenticationCredentialsNotFoundException("JWT is incorrect", e);
        }
    }

    public String getJwtFromHeader(String header) {
        if (header == null || !header.startsWith("Bearer "))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JWT header is incorrect");
        }
        return header.substring(7);
    }
}
