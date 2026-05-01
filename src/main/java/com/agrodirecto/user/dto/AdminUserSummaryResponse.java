package com.agrodirecto.user.dto;

import java.util.List;

public record AdminUserSummaryResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        String status,
        String primaryRole,
        List<String> roles,
        Boolean emailVerified,
        Boolean phoneVerified
) {
}
