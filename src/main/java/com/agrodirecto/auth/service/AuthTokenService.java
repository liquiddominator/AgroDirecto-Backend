package com.agrodirecto.auth.service;

import com.agrodirecto.auth.dto.AuthResponse;
import com.agrodirecto.auth.dto.AuthUserResponse;
import com.agrodirecto.common.exception.UnauthorizedException;
import com.agrodirecto.security.jwt.AuthenticatedUser;
import com.agrodirecto.security.jwt.JwtService;
import com.agrodirecto.user.entity.RefreshToken;
import com.agrodirecto.user.entity.User;
import com.agrodirecto.user.entity.UserRole;
import com.agrodirecto.user.repository.RefreshTokenRepository;
import com.agrodirecto.user.repository.UserRepository;
import com.agrodirecto.user.repository.UserRoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthTokenService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int REFRESH_TOKEN_BYTES = 48;

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final long refreshTokenDays;

    public AuthTokenService(
            JwtService jwtService,
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            @Value("${security.jwt.refresh-token-days}") long refreshTokenDays
    ) {
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.refreshTokenDays = refreshTokenDays;
    }

    @Transactional
    public AuthResponse issueTokenPair(User user, HttpServletRequest request) {
        List<String> roles = getRoles(user.getId());
        JwtService.GeneratedAccessToken accessToken = jwtService.generateAccessToken(user, roles);
        GeneratedRefreshToken refreshToken = createRefreshToken(user, request);

        return new AuthResponse(
                "Bearer",
                accessToken.token(),
                accessToken.expiresAt(),
                refreshToken.token(),
                toInstant(refreshToken.entity().getExpiresAt()),
                buildUserResponse(user, roles)
        );
    }

    @Transactional
    public AuthResponse refresh(String rawRefreshToken, HttpServletRequest request) {
        RefreshToken currentToken = refreshTokenRepository.findByTokenHash(hash(rawRefreshToken))
                .orElseThrow(() -> new UnauthorizedException("Refresh token invalido"));

        LocalDateTime now = LocalDateTime.now();
        if (currentToken.isRevoked() || currentToken.isExpired(now)) {
            throw new UnauthorizedException("Refresh token expirado o revocado");
        }

        User user = currentToken.getUser();
        GeneratedRefreshToken newRefreshToken = createRefreshToken(user, request);
        currentToken.setRevokedAt(now);
        currentToken.setReplacedBy(newRefreshToken.entity());
        refreshTokenRepository.save(currentToken);

        List<String> roles = getRoles(user.getId());
        JwtService.GeneratedAccessToken accessToken = jwtService.generateAccessToken(user, roles);

        return new AuthResponse(
                "Bearer",
                accessToken.token(),
                accessToken.expiresAt(),
                newRefreshToken.token(),
                toInstant(newRefreshToken.entity().getExpiresAt()),
                buildUserResponse(user, roles)
        );
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        refreshTokenRepository.findByTokenHash(hash(rawRefreshToken))
                .filter(token -> !token.isRevoked())
                .ifPresent(token -> {
                    token.setRevokedAt(LocalDateTime.now());
                    refreshTokenRepository.save(token);
                });
    }

    @Transactional(readOnly = true)
    public AuthUserResponse currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser principal)) {
            throw new UnauthorizedException("No existe una sesion autenticada");
        }

        User user = userRepository.findById(principal.id())
                .orElseThrow(() -> new UnauthorizedException("Usuario autenticado no existe"));
        return buildUserResponse(user, getRoles(user.getId()));
    }

    private GeneratedRefreshToken createRefreshToken(User user, HttpServletRequest request) {
        String rawToken = generateOpaqueToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hash(rawToken));
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(refreshTokenDays));
        refreshToken.setUserAgent(resolveUserAgent(request));
        refreshToken.setIpAddress(resolveIpAddress(request));

        return new GeneratedRefreshToken(rawToken, refreshTokenRepository.save(refreshToken));
    }

    private AuthUserResponse buildUserResponse(User user, List<String> roles) {
        String primaryRole = userRoleRepository.findPrimaryWithRoleByUserId(user.getId())
                .map(UserRole::getRole)
                .map(role -> role.getCode())
                .orElse(roles.isEmpty() ? null : roles.getFirst());

        return new AuthUserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getStatus().getCode(),
                primaryRole,
                roles
        );
    }

    private List<String> getRoles(Long userId) {
        return userRoleRepository.findAllWithRoleByUserId(userId).stream()
                .map(UserRole::getRole)
                .map(role -> role.getCode())
                .toList();
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[REFRESH_TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo calcular el hash del refresh token", exception);
        }
    }

    private String resolveUserAgent(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return request.getHeader("User-Agent");
    }

    private String resolveIpAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private Instant toInstant(LocalDateTime value) {
        return value.atZone(ZoneId.systemDefault()).toInstant();
    }

    private record GeneratedRefreshToken(String token, RefreshToken entity) {
    }
}
