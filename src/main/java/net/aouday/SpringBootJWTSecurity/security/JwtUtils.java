package net.aouday.SpringBootJWTSecurity.security;

import net.aouday.SpringBootJWTSecurity.entities.User;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtUtils {

    private final SecretKey secretKey;
    private final Duration jwtExpirationTime;
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public JwtUtils(
            @Value("${spring.jwt.signing-key}") String secretKey,
            @Value("${spring.jwt.expiration-minutes}") Long jwtExpirationTime
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.jwtExpirationTime = Duration.ofMinutes(jwtExpirationTime);
    }


    public String createToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationTime.toMillis());

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("id", user.getId())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }


    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e){
            logger.warn("Expired JWT token");
        } catch (UnsupportedJwtException e){
            logger.warn("Unsupported JWT token");
        } catch (MalformedJwtException e){
            logger.warn("Malformed JWT token");
        } catch (SignatureException e){
            logger.warn("Invalid JWT token");
        } catch (IllegalArgumentException e){
            logger.warn("Token is empty");
        }

        return false;
    }


    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    public String getRoleFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);

        return claims.get("role", String.class);
    }


    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
