package net.aouday.SpringBootJWTSecurity;

import net.aouday.SpringBootJWTSecurity.entities.User;
import net.aouday.SpringBootJWTSecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SpringBootJWTSecurityApplication implements ApplicationRunner {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootJWTSecurityApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@doe.com");
        user.setPassword(passwordEncoder.encode("password"));
//        userRepository.save(user);
    }
}
