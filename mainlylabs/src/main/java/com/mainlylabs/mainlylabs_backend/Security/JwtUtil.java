package com.mainlylabs.mainlylabs_backend.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final Key secretKey;
    private final long expiration;

    // Inyectamos las propiedades desde application.properties
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    /**
     * Genera un token JWT con username y roles.
     */
    public String generateToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles) // guardamos roles como lista de strings
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida un token y devuelve los claims.
     */
    public Jws<Claims> validateToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }

    /**
     * Extrae el username (subject) del token.
     */
    public String getUsernameFromToken(String token) {
        return validateToken(token).getBody().getSubject();
    }

    /**
     * Extrae los roles del token.
     */
    public List<String> getRolesFromToken(String token) {
        return validateToken(token).getBody().get("roles", List.class);
    }
}
