package net.aouday.SpringBootJWTSecurity.services;

import net.aouday.SpringBootJWTSecurity.dto.LoginRequest;
import net.aouday.SpringBootJWTSecurity.dto.RegisterRequest;
import net.aouday.SpringBootJWTSecurity.dto.TokenDTO;
import net.aouday.SpringBootJWTSecurity.entities.User;
import net.aouday.SpringBootJWTSecurity.exceptions.UserAlreadyExistsException;
import net.aouday.SpringBootJWTSecurity.repository.UserRepository;
import net.aouday.SpringBootJWTSecurity.security.CustomUserDetails;
import net.aouday.SpringBootJWTSecurity.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthentificationServiceImpl implements AuthentificationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthentificationServiceImpl(
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RefreshTokenService refreshTokenService
            ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }


    @Override
    public TokenDTO login(LoginRequest loginRequest) {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();

            String token = jwtUtils.createToken(user);
            String refreshToken = refreshTokenService.create(user);

            return new TokenDTO(token,refreshToken);
    }

    @Transactional
    @Override
    public void register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User already exists by email");
        }

        User user = new User();

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }
}
