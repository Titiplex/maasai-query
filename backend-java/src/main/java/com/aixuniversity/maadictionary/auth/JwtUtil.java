package com.aixuniversity.maadictionary.auth;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
public class JwtUtil {
    private final SecretKey key = Keys.hmacShaKeyFor(
            Base64.getDecoder().decode(System.getenv().getOrDefault("JWT_SECRET",
                    "9uEZ2M87...==")));                   // 256 bit

    public String generate(String username, List<String> roles) {
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(1, DAYS)))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}