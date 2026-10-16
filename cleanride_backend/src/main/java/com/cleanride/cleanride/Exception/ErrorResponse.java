package com.cleanride.cleanride.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Az összes hibaválasz egységes alakja.
 * A fieldErrors csak validációs hibánál van kitöltve, egyébként null.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        Map<String, String> fieldErrors
) {
}
