package fr.albanj.corelib.core;

import java.time.Instant;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ExceptionMessage {

    @NonNull
    private Integer code;
    @NonNull
    private Integer httpStatus;
    @NonNull
    private String message;
    @NonNull
    private String source;
    private Instant timestamp = Instant.now();

}
