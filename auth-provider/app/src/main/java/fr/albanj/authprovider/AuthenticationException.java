package fr.albanj.authprovider;

import org.springframework.http.HttpStatus;

import fr.albanj.corelib.core.ApiException;

public class AuthenticationException extends ApiException {

    public AuthenticationException(String message) {
        super(1, HttpStatus.UNAUTHORIZED, message);
    }
}
