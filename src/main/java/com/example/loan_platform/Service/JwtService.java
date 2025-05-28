package com.example.loan_platform.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JwtService {

    // application.properties'den alınan gizli anahtar (base64 veya uzun string olmalı)
    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    // Token geçerlilik süresi (milisaniye cinsinden)
    @Value("${jwt.expiration}")
    private long JWT_EXPIRATION;

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    // JWT imzalama anahtarını oluşturur.
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // Token içerisinden kullanıcı adını (subject) çıkarır.
    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
    }

    // Token geçerliliğini kontrol eder.
// Tokenın username ile userDetails eşleşmeli ve token süresi dolmamış olmalı.
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Tokenın süresinin dolup dolmadığını kontrol eder.
    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey())
                .build().parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }

    /*
      Kullanıcı detaylarından JWT token üretir.
      Subject olarak kullanıcı adı atanır.
      Kullanıcının rolleri "roles" claim olarak liste halinde tokena eklenir.
      Token id (jti) benzersiz UUID ile oluşturulur.
      Tokenın son kullanma tarihi JWT_EXPIRATION kadar ileriye ayarlanır.
      HS256 algoritması ile imzalanır.
    */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder().setSubject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList())).setIssuedAt(new Date())
                .setId(UUID.randomUUID().toString())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 saat geçerli
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

}

