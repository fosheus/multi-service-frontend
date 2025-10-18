package fr.albanj.authprovider;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.albanj.corelib.servicecore.LoginRequest;
import fr.albanj.corelib.servicecore.TokenResponse;
import fr.albanj.corelib.servicecore.ValidRequest;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        return authService.authenticate(request);
    }

    @PostMapping("/valid")
    public void valid(@RequestBody ValidRequest request) {
        authService.validate(request);
    }

}
