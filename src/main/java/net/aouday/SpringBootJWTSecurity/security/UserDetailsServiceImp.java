package net.aouday.SpringBootJWTSecurity.security;

import net.aouday.SpringBootJWTSecurity.entities.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import net.aouday.SpringBootJWTSecurity.repository.UserRepository;

@Component
public class UserDetailsServiceImp implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(()->{
            return new UsernameNotFoundException("User Not Found");
        }) ;

        return new CustomUserDetails(user);
    }
}
