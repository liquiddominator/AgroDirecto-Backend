package com.agrodirecto.security.jwt;

import java.time.Instant;
import java.util.List;

public record JwtClaims(
        Long userId,
        String email,
        List<String> roles,
        Instant issuedAt,
        Instant expiresAt
) {
}
