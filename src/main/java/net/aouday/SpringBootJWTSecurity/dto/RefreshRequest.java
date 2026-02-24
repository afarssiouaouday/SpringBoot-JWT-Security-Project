package net.aouday.SpringBootJWTSecurity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class RefreshRequest {
    @NotBlank
    private String refreshToken;

}
