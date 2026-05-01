package com.agrodirecto.user.dto;

import com.agrodirecto.verification.dto.VerificationDocumentResponse;
import java.util.List;

public record AdminUserDetailResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        String status,
        String primaryRole,
        List<String> roles,
        Boolean acceptedTerms,
        Boolean acceptedPrivacyPolicy,
        Boolean emailVerified,
        Boolean phoneVerified,
        Object profile,
        List<VerificationDocumentResponse> verificationDocuments
) {
}
