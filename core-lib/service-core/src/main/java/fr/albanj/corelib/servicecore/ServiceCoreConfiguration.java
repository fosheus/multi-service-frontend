package fr.albanj.corelib.servicecore;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import fr.albanj.corelib.servicecore.filter.AuthProviderValidationFilter;
import fr.albanj.corelib.servicecore.filter.RemoteAuthenticationFilter;
import fr.albanj.corelib.servicecore.filter.RemoteAuthenticationSuccessHandler;

@Configuration
@ComponentScan
@EnableWebSecurity
@EnableAutoConfiguration(exclude = { UserDetailsServiceAutoConfiguration.class })
@EnableMethodSecurity(jsr250Enabled = true)
public class ServiceCoreConfiguration {

    private final AuthProviderValidationFilter authProviderValidationFilter;

    public ServiceCoreConfiguration(AuthProviderValidationFilter authProviderValidationFilter) {
        this.authProviderValidationFilter = authProviderValidationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, RemoteAuthenticationFilter remoteAuthFilter)
            throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/auth/login", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(authProviderValidationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(remoteAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public RemoteAuthenticationFilter remoteAuthFilter(AuthenticationManager authenticationManager,
            @Value("${auth.provider.url}") String authProviderUrl,
            @Value("${auth.success.redirect}") String authSuccessRedirect) {

        RemoteAuthenticationFilter remoteAuthenticationFilter = new RemoteAuthenticationFilter(authenticationManager,
                authProviderUrl);
        remoteAuthenticationFilter
                .setAuthenticationSuccessHandler(new RemoteAuthenticationSuccessHandler(authSuccessRedirect));
        remoteAuthenticationFilter.setRequiresAuthenticationRequestMatcher(
                new AntPathRequestMatcher("/login", "POST") // 🔒 n'intercepte que POST /login
        );
        return remoteAuthenticationFilter;

    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            // L’authentification est gérée dans RemoteAuthenticationFilter directement
            return authentication;
        };
    }

}
