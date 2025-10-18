package fr.albanj.corelib.servicecore;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtils {

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

        Claims claims = Jwts.parser()
                .unsecured()
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

}
