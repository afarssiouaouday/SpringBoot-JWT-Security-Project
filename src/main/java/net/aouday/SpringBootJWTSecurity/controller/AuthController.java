package net.aouday.SpringBootJWTSecurity.controller;

import jakarta.validation.Valid;
import net.aouday.SpringBootJWTSecurity.dto.LoginRequest;
import net.aouday.SpringBootJWTSecurity.dto.RefreshRequest;
import net.aouday.SpringBootJWTSecurity.dto.RegisterRequest;
import net.aouday.SpringBootJWTSecurity.dto.TokenDTO;
import net.aouday.SpringBootJWTSecurity.entities.RefreshToken;
import net.aouday.SpringBootJWTSecurity.security.JwtUtils;
import net.aouday.SpringBootJWTSecurity.services.AuthentificationService;
import net.aouday.SpringBootJWTSecurity.services.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    final AuthentificationService authentificationService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(
            JwtUtils jwtUtils,
            AuthenticationManager authenticationManager,
            AuthentificationService authentificationService,
            RefreshTokenService refreshTokenService
    ) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.authentificationService = authentificationService;
        this.refreshTokenService = refreshTokenService;
    }


    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginRequest loginRequest) {

            return ResponseEntity.ok(authentificationService.login(loginRequest));
        }


    @PostMapping("/refresh")
    public ResponseEntity<TokenDTO>  refresh(@Valid @RequestBody RefreshRequest refreshRequest) {
        RefreshToken rt = refreshTokenService.validate(refreshRequest.getRefreshToken());

        String newRefreshToken = refreshTokenService.rotate(rt);

        String newAccess = jwtUtils.createToken(rt.getUser());

        return ResponseEntity.ok(new TokenDTO(newAccess, newRefreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {

        RefreshToken rt = refreshTokenService.validate(request.getRefreshToken());
        refreshTokenService.revoke(rt);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {

        System.out.println(request);

        authentificationService.register(request);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    }
