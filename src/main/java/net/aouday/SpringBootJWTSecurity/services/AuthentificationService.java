package net.aouday.SpringBootJWTSecurity.services;

import net.aouday.SpringBootJWTSecurity.dto.LoginRequest;
import net.aouday.SpringBootJWTSecurity.dto.RegisterRequest;
import net.aouday.SpringBootJWTSecurity.dto.TokenDTO;

public interface AuthentificationService {
    public TokenDTO login(LoginRequest loginRequest);
    public void register(RegisterRequest request);
}
