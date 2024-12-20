package com.blessedbits.SchoolHub.security;

import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class SecurityConstants {
    public static final long ACCESS_TOKEN_VALIDITY = 10*60*1000;
    public static final long REFRESH_TOKEN_VALIDITY = 30L*60*60*24*1000;
    public static final String JWT_SECRET = "5pcGFBfkSBNpFrtgPNAl3FzpGygSwdDaLtwUQWPt";
    public static final Key SIGNING_KEY = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
}
