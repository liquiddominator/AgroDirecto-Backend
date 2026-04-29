package com.agrodirecto.auth.service;

import com.agrodirecto.auth.dto.AuthResponse;
import com.agrodirecto.auth.dto.AuthUserResponse;
import com.agrodirecto.auth.dto.LoginRequest;
import com.agrodirecto.common.exception.UnauthorizedException;
import com.agrodirecto.user.entity.User;
import com.agrodirecto.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthTokenService authTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authTokenService = authTokenService;
    }

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest servletRequest) {
        User user = userRepository.findByEmailIgnoreCase(normalizeEmail(request.email()))
                .orElseThrow(() -> new UnauthorizedException("Credenciales invalidas"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Credenciales invalidas");
        }

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new UnauthorizedException("El usuario se encuentra bloqueado temporalmente");
        }

        String status = user.getStatus().getCode();
        if ("SUSPENDED".equals(status) || "LOCKED".equals(status)) {
            throw new UnauthorizedException("El usuario no esta habilitado para iniciar sesion");
        }

        return authTokenService.issueTokenPair(user, servletRequest);
    }

    @Transactional
    public AuthResponse refresh(String refreshToken, HttpServletRequest servletRequest) {
        return authTokenService.refresh(refreshToken, servletRequest);
    }

    @Transactional
    public void logout(String refreshToken) {
        authTokenService.logout(refreshToken);
    }

    @Transactional(readOnly = true)
    public AuthUserResponse me() {
        return authTokenService.currentUser();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
