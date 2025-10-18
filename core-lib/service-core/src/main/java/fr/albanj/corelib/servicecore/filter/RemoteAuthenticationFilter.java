package fr.albanj.corelib.servicecore.filter;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import fr.albanj.corelib.servicecore.LoginRequest;
import fr.albanj.corelib.servicecore.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RemoteAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final String authProviderUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public RemoteAuthenticationFilter(
            AuthenticationManager authenticationManager,
            String authProviderUrl) {
        this.authProviderUrl = authProviderUrl;
        setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/login"); // ou /auth/login si tu veux
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        try {
            ResponseEntity<TokenResponse> result = restTemplate.postForEntity(
                    authProviderUrl + "/api/login",
                    new LoginRequest(username, password), TokenResponse.class);

            if (result.getStatusCode().is2xxSuccessful() && result.getBody() != null) {
                String token = result.getBody().getToken();
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, token,
                        List.of());
                return auth;
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Erreur d'authentification", e);
        }

        throw new BadCredentialsException("Identifiants invalides");
    }
}
