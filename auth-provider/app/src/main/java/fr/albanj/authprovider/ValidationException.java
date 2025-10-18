package fr.albanj.authprovider;

import org.springframework.http.HttpStatus;

import fr.albanj.corelib.core.ApiException;

public class ValidationException extends ApiException {

    public ValidationException(String message) {
        super(2, HttpStatus.UNAUTHORIZED, message);
    }
}
