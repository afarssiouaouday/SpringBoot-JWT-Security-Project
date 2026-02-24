package net.aouday.SpringBootJWTSecurity.exceptions.handlers;

import jakarta.servlet.http.HttpServletRequest;
import net.aouday.SpringBootJWTSecurity.dto.ApiError;
import net.aouday.SpringBootJWTSecurity.exceptions.RefreshTokenInvalidException;
import net.aouday.SpringBootJWTSecurity.exceptions.UserAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    private ApiError buildError(HttpStatus status, String message, HttpServletRequest request, Map<String, String> errors) {
        return new ApiError(
                Instant.now(),
                status.value(),
                message,
                request.getRequestURI(),
                (errors == null || errors.isEmpty()) ? null : errors
        );
    }

    private ResponseEntity<ApiError> respond(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(buildError(status, message, request, null));
    }

    private ResponseEntity<ApiError> respond(HttpStatus status, String message, HttpServletRequest request, Map<String, String> errors) {
        return ResponseEntity.status(status).body(buildError(status, message, request, errors));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        return respond(HttpStatus.UNAUTHORIZED, "Invalid email or password", request);
    }


    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExistsException(UserAlreadyExistsException e, HttpServletRequest request) {
        return respond(HttpStatus.CONFLICT, e.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(err -> {
            // garde la 1ère erreur par champ (pas d'écrasement)
            fieldErrors.putIfAbsent(err.getField(), err.getDefaultMessage());
        });

        return respond(HttpStatus.BAD_REQUEST, "Validation failed", request, fieldErrors);
    }

    @ExceptionHandler(RefreshTokenInvalidException.class)
    public ResponseEntity<ApiError> handleRefreshTokenInvalid(RefreshTokenInvalidException e, HttpServletRequest request) {
        return respond(HttpStatus.UNAUTHORIZED, e.getMessage(), request);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception e, HttpServletRequest request) {
        logger.error("Unhandled exception at {} {}", request.getMethod(), request.getRequestURI(), e);

        // Message générique côté client
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request);
    }
}
