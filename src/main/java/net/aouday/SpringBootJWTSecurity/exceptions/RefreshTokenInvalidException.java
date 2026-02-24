package net.aouday.SpringBootJWTSecurity.exceptions;

public class RefreshTokenInvalidException extends RuntimeException {
    public RefreshTokenInvalidException(String message) {
        super(message);
    }
}
