package fr.albanj.corelib.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalErrorResponseAdvice {

    private final String applicationName;

    public GlobalErrorResponseAdvice(@Value("${spring.application.name}") String applicationName) {
        this.applicationName = applicationName;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMessage> handleException(Exception e) {

        return handleApiException(new ApiException("Erreur non gérée : " + e.getMessage(), e));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionMessage> handleApiException(ApiException e) {

        log.error("Api Exception", e);
        String message = e.getMessage();
        if (e.getPublicMessage() != null) {
            message = e.getPublicMessage();
        }

        return ResponseEntity.status(e.getHttpStatus())
                .body(new ExceptionMessage(e.getCode(), e.getHttpStatus().value(), message, applicationName));

    }

    @ExceptionHandler(ServiceApiException.class)
    public ResponseEntity<ExceptionMessage> handleServiceApiException(ServiceApiException e) {

        log.error("Erreur issue du service {}", e.getSource(), e);

        return ResponseEntity.status(e.getExceptionMessage().getHttpStatus()).body(e.getExceptionMessage());

    }
}
