package fr.albanj.authprovider;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.albanj.corelib.servicecore.JwtUtils;
import fr.albanj.corelib.servicecore.TokenExpiredException;
import fr.albanj.corelib.servicecore.UserAuthMetadata;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

    private final String secret;
    private final Duration sessionDuration;

    public JwtService(
            @Value("${auth.jwt.secret}") String secret,
            @Value("${auth.jwt.session-duration}") Duration sessionDuration) {
        this.secret = secret;
        this.sessionDuration = sessionDuration;
    }

    @PostConstruct
    public void validate() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("La clé jwt doit faire au moins 32 char");
        }
    }

    public String generateToken(UserAuthMetadata userAuthMetadata) {
        return JwtUtils.generateToken(secret, sessionDuration, userAuthMetadata);
    }

    public UserAuthMetadata verifyToken(String token) throws TokenExpiredException {
        return JwtUtils.verifyToken(secret, token);
    }
}
