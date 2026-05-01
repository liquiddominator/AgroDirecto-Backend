package com.agrodirecto.verification.controller;

import com.agrodirecto.verification.dto.ReviewDecisionRequest;
import com.agrodirecto.verification.dto.VerificationDocumentResponse;
import com.agrodirecto.verification.service.VerificationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/verifications")
public class AdminVerificationController {

    private final VerificationService verificationService;

    public AdminVerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @GetMapping("/requests")
    public ResponseEntity<List<VerificationDocumentResponse>> requests() {
        return ResponseEntity.ok(verificationService.listVerificationRequests());
    }

    @PostMapping("/documents/{documentId}/review")
    public ResponseEntity<VerificationDocumentResponse> review(
            @PathVariable("documentId") Long documentId,
            @Valid @RequestBody ReviewDecisionRequest request
    ) {
        return ResponseEntity.ok(verificationService.reviewDocument(documentId, request));
    }
}
