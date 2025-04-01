package ru.project.subtrack.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final String SECRET_KEY = "secret-key-should-be-long-and-random-1234567890"; // поменяй на свою секретку
    private final long EXPIRATION_TIME = 86400000; // 1 день в мс

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Генерация токена
    public String generateToken(UUID userId, String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Извлечение userId из токена
    public String extractUserId(String token) {
        System.out.println("Извлекаем userId из токена: " + token);
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId", String.class);
        } catch (Exception e) {
            System.out.println("Ошибка извлечения userId: " + e.getMessage());
            return null;
        }
    }


    // Проверка токена
    public boolean validateToken(String token) {
        System.out.println("Проверяем токен: " + token);
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            System.out.println("Ошибка валидации JWT: " + e.getMessage());
            return false;
        }
    }
}
