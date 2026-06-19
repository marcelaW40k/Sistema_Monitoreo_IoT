package com.sistemaMonitoreo.backend.controller;

import com.sistemaMonitoreo.backend.DTO.LoginRequestDTO;
import com.sistemaMonitoreo.backend.DTO.LoginResponseDTO;
import com.sistemaMonitoreo.backend.model.User;
import com.sistemaMonitoreo.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user));
    }
}