package net.aouday.SpringBootJWTSecurity.services;

import net.aouday.SpringBootJWTSecurity.entities.RefreshToken;
import net.aouday.SpringBootJWTSecurity.entities.User;
import net.aouday.SpringBootJWTSecurity.exceptions.RefreshTokenInvalidException;
import net.aouday.SpringBootJWTSecurity.repository.ReshreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;


@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final ReshreshTokenRepository repository;
    private final Duration refreshDuration;

    public RefreshTokenServiceImpl(
            ReshreshTokenRepository repo,
            @Value("${spring.jwt.refresh-expiration-days}") long days
    ) {
        this.repository = repo;
        this.refreshDuration = Duration.ofDays(days);
    }

    @Override
    public String create(User user) {
        String raw = UUID.randomUUID() + "." + UUID.randomUUID();

        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setTokenHash(hash(raw));
        rt.setExpiryDate(new Date(System.currentTimeMillis() + refreshDuration.toMillis()));
        rt.setRevoked(false);

        repository.save(rt);
        return raw;
    }

    @Override
    public RefreshToken validate(String rawToken) {
        String tokenHash = hash(rawToken);

        RefreshToken rt = repository.findByTokenHash(tokenHash).
                orElseThrow(()-> new RefreshTokenInvalidException("Invalid Refresh Token"));

        if(rt.isRevoked()) throw new RefreshTokenInvalidException("Refresh Token is revoked");
        if(rt.getExpiryDate().before(new Date())) throw new RefreshTokenInvalidException("Refresh Token is expired");

        return rt;

    }

    @Override
    public void revoke(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        repository.save(refreshToken);
    }

    @Override
    public String rotate(RefreshToken old) {
        revoke(old);
        return create(old.getUser());
    }

    private String hash(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
