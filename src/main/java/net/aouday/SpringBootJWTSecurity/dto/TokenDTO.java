package net.aouday.SpringBootJWTSecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TokenDTO {
    String accessToken;
    String refreshToken;

}
