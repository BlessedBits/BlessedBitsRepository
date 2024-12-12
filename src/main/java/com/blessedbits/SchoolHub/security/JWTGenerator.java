package com.blessedbits.SchoolHub.security;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTGenerator {
    public String generateJWT(Authentication auth) {
        String username = auth.getName();
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + SecurityConstants.JWT_TOKEN_VALIDITY * 1000);

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
}
