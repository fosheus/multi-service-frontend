package fr.albanj.corelib.servicecore;

import java.util.List;

public record UserAuthMetadata(int loginId, String username, String fullname, List<String> privileges) {
}
