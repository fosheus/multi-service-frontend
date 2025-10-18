package fr.albanj.authprovider;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import fr.albanj.corelib.servicecore.LoginRequest;
import fr.albanj.corelib.servicecore.TokenExpiredException;
import fr.albanj.corelib.servicecore.UserAuthMetadata;
import fr.albanj.corelib.servicecore.TokenResponse;
import fr.albanj.corelib.servicecore.ValidRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthService(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public TokenResponse authenticate(LoginRequest request) {

        Optional<User> optUser = userService.findByLogin(request.getLogin());

        if (optUser.isEmpty()) {
            throw new AuthenticationException("Aucun utilisateur existant avec ce login");
        }
        User user = optUser.get();
        if (!BCrypt.checkpw(request.getPassword(), user.password())) {
            throw new AuthenticationException("Aucun utilisateur existant avec ce login");
        }

        return new TokenResponse(
                jwtService
                        .generateToken(
                                new UserAuthMetadata(user.id(), user.login(), user.fullname(), user.privileges())));
    }

    public void validate(ValidRequest request) {
        try {
            jwtService.verifyToken(request.getToken());
        } catch (TokenExpiredException e) {
            throw new ValidationException("Token expiré");
        } catch (Exception e) {
            log.debug("Impossible de valider le token : {}", request.getToken());
            throw new ValidationException("Impossible de valider le token");
        }
    }

}
