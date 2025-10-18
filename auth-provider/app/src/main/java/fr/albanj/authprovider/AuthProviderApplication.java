package fr.albanj.authprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import fr.albanj.corelib.core.CorelibConfiguration;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
@Import({ CorelibConfiguration.class })
public class AuthProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthProviderApplication.class, args);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());

        return http.build();
    }

}
