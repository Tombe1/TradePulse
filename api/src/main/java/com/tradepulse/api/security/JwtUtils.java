package com.tradepulse.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // במציאות זה צריך להיות בקובץ הגדרות, כאן נשים מפתח חזק לדוגמה
    private static final String SECRET = "MySuperSecretKeyForTradePulseProjectWhichIsVeryLong123!";
    private static final long EXPIRATION_TIME = 86400000; // 24 שעות

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // יצירת טוקן עבור שם משתמש
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // חילוץ שם משתמש מהטוקן
    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // בדיקה האם הטוקן תקין
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}