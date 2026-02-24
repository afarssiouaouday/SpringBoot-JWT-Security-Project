package net.aouday.SpringBootJWTSecurity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")

public class TestController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String test() {

        return "test";
    }
}
