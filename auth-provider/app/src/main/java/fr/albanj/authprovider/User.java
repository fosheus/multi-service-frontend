package fr.albanj.authprovider;

import java.util.List;

public record User(Integer id, String login, String password, String fullname, List<String> privileges) {
}
