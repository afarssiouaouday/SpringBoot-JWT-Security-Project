package net.aouday.SpringBootJWTSecurity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(Instant timestamp,
                       int status,
                       String error,
                       String path,
                       Map<String, String> errors) {
}
