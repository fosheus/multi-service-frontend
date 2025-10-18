package fr.albanj.corelib.core;

import jakarta.annotation.Nonnull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceApiException extends RuntimeException {

    @NonNull
    private ExceptionMessage exceptionMessage;
    @NonNull
    private String source;

    public ServiceApiException(String message, @Nonnull String source, @Nonnull ExceptionMessage exceptionMessage) {
        super(source);
        this.source = source;
        this.exceptionMessage = exceptionMessage;
    }

    public ServiceApiException(String message, String source, ExceptionMessage exceptionMessage, Throwable t) {
        super(source, t);
        this.source = source;
        this.exceptionMessage = exceptionMessage;
    }

}
