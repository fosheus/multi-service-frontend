package fr.albanj.corelib.servicecore;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String generateToken(String secret, Duration sessionDuration, UserAuthMetadata tokenPayload) {
        return Jwts.builder()
                .subject(tokenPayload.username())
                .claim("loginId", tokenPayload.loginId())
                .claim("fullname", tokenPayload.fullname())
                .claim("privileges", tokenPayload.privileges())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(sessionDuration)))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public static UserAuthMetadata verifyToken(String secret, String token) throws TokenExpiredException {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (claims.getExpiration().before(new Date())) {
            throw new TokenExpiredException();
        }

        return new UserAuthMetadata(
                claims.get("loginId", Integer.class),
                claims.getSubject(),
                claims.get("fullname", String.class),
                (List<String>) claims.get("privileges", List.class));
    }

    public static UserAuthMetadata parseTokenUnsecured(String token) throws TokenExpiredException {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("JWT invalide (il doit contenir au moins deux parties)");
            }

            // partie payload = 2e segment
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> claims = objectMapper.readValue(payloadJson, Map.class);

            int loginId = ((Number) claims.getOrDefault("loginId", 0)).intValue();
            String username = (String) claims.getOrDefault("sub", null);
            String fullname = (String) claims.getOrDefault("fullname", null);

            List<String> privileges = (List<String>) claims.getOrDefault("privileges", List.of());

            return new UserAuthMetadata(loginId, username, fullname, privileges);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du parsing du JWT", e);
        }
    }

}
