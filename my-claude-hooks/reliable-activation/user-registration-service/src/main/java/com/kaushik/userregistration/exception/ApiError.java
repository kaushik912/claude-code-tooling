package com.kaushik.userregistration.exception;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String message,
        List<String> details
) {
    public ApiError(int status, String message, List<String> details) {
        this(Instant.now(), status, message, details);
    }
}
