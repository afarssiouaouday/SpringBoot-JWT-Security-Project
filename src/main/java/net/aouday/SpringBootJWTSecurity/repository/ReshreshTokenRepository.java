package net.aouday.SpringBootJWTSecurity.repository;

import net.aouday.SpringBootJWTSecurity.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import net.aouday.SpringBootJWTSecurity.entities.User;

import java.util.Optional;

public interface ReshreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void deleteByUser(User user);
}
