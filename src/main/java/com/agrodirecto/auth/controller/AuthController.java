package com.agrodirecto.auth.controller;

import com.agrodirecto.auth.dto.AuthResponse;
import com.agrodirecto.auth.dto.AuthUserResponse;
import com.agrodirecto.auth.dto.LoginRequest;
import com.agrodirecto.auth.dto.LogoutRequest;
import com.agrodirecto.auth.dto.RefreshTokenRequest;
import com.agrodirecto.auth.dto.RegisterRequest;
import com.agrodirecto.auth.service.AuthService;
import com.agrodirecto.auth.service.AuthRegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthRegistrationService authRegistrationService;
    private final AuthService authService;

    public AuthController(AuthRegistrationService authRegistrationService, AuthService authService) {
        this.authRegistrationService = authRegistrationService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest servletRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authRegistrationService.register(request, servletRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest
    ) {
        return ResponseEntity.ok(authService.login(request, servletRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest servletRequest
    ) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken(), servletRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthUserResponse> me() {
        return ResponseEntity.ok(authService.me());
    }
}
