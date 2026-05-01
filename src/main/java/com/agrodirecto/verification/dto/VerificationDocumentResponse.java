package com.agrodirecto.verification.dto;

import java.time.LocalDateTime;

public record VerificationDocumentResponse(
        Long id,
        Long userId,
        String userFullName,
        String userEmail,
        String userStatus,
        String documentTypeCode,
        String documentTypeName,
        String documentNumber,
        String originalFilename,
        String mimeType,
        String downloadUrl,
        LocalDateTime uploadedAt,
        VerificationReviewResponse latestReview
) {
}
