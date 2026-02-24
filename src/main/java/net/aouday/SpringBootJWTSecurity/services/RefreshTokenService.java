package net.aouday.SpringBootJWTSecurity.services;

import net.aouday.SpringBootJWTSecurity.entities.RefreshToken;
import net.aouday.SpringBootJWTSecurity.entities.User;

public interface RefreshTokenService {
    String create(User user);
    RefreshToken validate(String rawToken);
    void revoke(RefreshToken refreshToken);
    String rotate(RefreshToken old);
}
