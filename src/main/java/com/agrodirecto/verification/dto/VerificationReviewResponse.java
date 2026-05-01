package com.agrodirecto.verification.dto;

import java.time.LocalDateTime;

public record VerificationReviewResponse(
        Long id,
        String decision,
        String reason,
        String evidenceUrl,
        Long reviewedByUserId,
        String reviewedByFullName,
        LocalDateTime reviewedAt
) {
}
