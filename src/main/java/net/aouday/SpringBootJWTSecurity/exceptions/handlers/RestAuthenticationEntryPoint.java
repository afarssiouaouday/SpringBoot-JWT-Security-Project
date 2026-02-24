package net.aouday.SpringBootJWTSecurity.exceptions.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = """
                {
                    "timestamp": "%s",
                    "status": %d,
                    "error": "%s",
                    "path": "%s"
                }
                """.formatted(
                Instant.now(),
                status.value(),
                authException.getMessage(),
                request.getRequestURI()
        );

        response.getWriter().write(json);
    }
}