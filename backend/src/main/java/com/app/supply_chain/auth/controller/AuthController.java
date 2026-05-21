package com.app.supply_chain.auth.controller;
import com.app.supply_chain.auth.model.User;
import com.app.supply_chain.auth.service.AuthService;
import com.app.supply_chain.auth.dto.LoginRequest;
import com.app.supply_chain.auth.dto.LoginResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {

        return authService.register(user);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
}

    @GetMapping("/test")
    public String test() {

        return "Secure endpoint working";
    }
}