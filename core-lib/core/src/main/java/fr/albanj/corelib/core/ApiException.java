package fr.albanj.corelib.core;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApiException extends RuntimeException {

    private int code = 0;
    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    private String publicMessage;

    public String toReadableString() {
        return "code=" + code + ", httpStatus=" + httpStatus.value() + ", message=" + this.getMessage() + ", public="
                + publicMessage;
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable t) {
        super(message, t);
    }

    public ApiException(int code, HttpStatus httpStatus, String message) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public static class Builder {

        private ApiException apiException;

        public Builder() {
            this.apiException = new ApiException("Internal server error");
        }

        public Builder(String message) {
            this.apiException = new ApiException(message);
        }

        public Builder(String message, Throwable t) {
            this.apiException = new ApiException(message, t);
        }

        public Builder withCode(int code) {
            this.apiException.code = code;
            return this;
        }

        public Builder withHttpStatus(HttpStatus httpStatus) {
            this.apiException.httpStatus = httpStatus;
            return this;
        }

        public Builder withPublicMessage(String publicMessage) {
            this.apiException.publicMessage = publicMessage;
            return this;
        }

        public Builder withPublicMessage(String publicMessage, List<Object> args) {
            this.apiException.publicMessage = String.format(publicMessage, args);
            return this;
        }

        public ApiException build() {
            return this.apiException;
        }
    }
}