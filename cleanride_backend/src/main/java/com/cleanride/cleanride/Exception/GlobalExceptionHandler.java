package com.cleanride.cleanride.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // @Valid hibák -> 400 mezőnkénti üzenetekkel
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> fieldErrors.putIfAbsent(fe.getField(), fe.getDefaultMessage()));

        ErrorResponse body = build(HttpStatus.BAD_REQUEST, "Érvénytelen kérés", fieldErrors);
        return ResponseEntity.badRequest().headers(headers).body(body);
    }

    // Unique constraint (pl. két egyidejű regisztráció ugyanazzal az email címmel) -> 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Adatbázis integritási hiba: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(build(HttpStatus.CONFLICT,
                        "Az adat ütközik egy már meglévő rekorddal (pl. az email vagy a telefonszám foglalt)",
                        null));
    }

    // Minden váratlan hiba -> 500, részletek nélkül a kliensnek
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Váratlan hiba", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(build(HttpStatus.INTERNAL_SERVER_ERROR, "Váratlan szerverhiba történt", null));
    }

    // A base osztály többi hibáját (ResponseStatusException, rossz JSON, 405 stb.)
    // ugyanebbe az ErrorResponse alakba csomagoljuk, a státuszkód változatlan marad.
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {

        String message = (body instanceof ProblemDetail pd && pd.getDetail() != null)
                ? pd.getDetail()
                : ex.getMessage();

        ErrorResponse response = build(HttpStatus.valueOf(statusCode.value()), message, null);
        return ResponseEntity.status(statusCode).headers(headers).body(response);
    }

    private ErrorResponse build(HttpStatus status, String message, Map<String, String> fieldErrors) {
        return new ErrorResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                message, fieldErrors);
    }
}