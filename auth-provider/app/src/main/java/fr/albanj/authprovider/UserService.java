package fr.albanj.authprovider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class UserService {

    private final Map<String, User> users = new HashMap<>();
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(BCryptVersion.$2Y);

    @PostConstruct
    public void init() {
        users.put("admin", new User(1, "admin", bCryptPasswordEncoder.encode("admin"), "Admin ADMIN",
                List.of("USER_SERVICE", "PRODUCT_SERVICE")));
    }

    public Optional<User> findByLogin(String login) {
        return Optional.ofNullable(users.get(login));
    }

}
