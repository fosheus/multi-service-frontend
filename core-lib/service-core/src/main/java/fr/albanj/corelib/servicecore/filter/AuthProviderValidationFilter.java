package fr.albanj.corelib.servicecore.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import fr.albanj.corelib.servicecore.JwtUtils;
import fr.albanj.corelib.servicecore.TokenExpiredException;
import fr.albanj.corelib.servicecore.UserAuthMetadata;
import fr.albanj.corelib.servicecore.ValidRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthProviderValidationFilter extends OncePerRequestFilter {

    private final String authProviderUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public AuthProviderValidationFilter(@Value("${auth.provider.url}") String authProviderUrl) {
        this.authProviderUrl = authProviderUrl;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractJwtFromCookie(request);
        if (token != null) {
            try {
                ResponseEntity<Void> resp = restTemplate.postForEntity(authProviderUrl + "/api/valid",
                        new ValidRequest(token), Void.class);
                if (resp.getStatusCode().is2xxSuccessful()) {
                    UserAuthMetadata metadata = JwtUtils.parseTokenUnsecured(token);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            metadata.username(),
                            null,
                            toAuthorities(metadata.privileges()));
                    authentication.setDetails(metadata);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // rien : le filtre laissera passer sans authent
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie c : request.getCookies()) {
            if ("AUTH_TOKEN".equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> toAuthorities(List<String> roles) {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toList());
    }

    public UserAuthMetadata parseToken(String token) throws TokenExpiredException {
        return JwtUtils.parseTokenUnsecured(token);
    }
}
