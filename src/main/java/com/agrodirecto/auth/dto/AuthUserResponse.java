package com.agrodirecto.auth.dto;

import java.util.List;

public record AuthUserResponse(
        Long id,
        String email,
        String fullName,
        String status,
        String primaryRole,
        List<String> roles
) {
}
