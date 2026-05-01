package com.agrodirecto.verification.controller;

import com.agrodirecto.verification.dto.VerificationDocumentResponse;
import com.agrodirecto.verification.service.VerificationService;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api/verification")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VerificationDocumentResponse> uploadDocument(
            @RequestParam("documentTypeCode") @NotBlank(message = "El tipo de documento es obligatorio") String documentTypeCode,
            @RequestParam(value = "documentNumber", required = false) String documentNumber,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(verificationService.uploadCurrentUserDocument(
                documentTypeCode,
                documentNumber,
                file
        ));
    }

    @GetMapping("/documents")
    public ResponseEntity<List<VerificationDocumentResponse>> myDocuments() {
        return ResponseEntity.ok(verificationService.listCurrentUserDocuments());
    }

    @GetMapping("/documents/{documentId}/file")
    public ResponseEntity<Resource> downloadDocument(@PathVariable("documentId") Long documentId) {
        VerificationService.FileDownload download = verificationService.loadDocumentFile(documentId);
        MediaType mediaType = download.mimeType() == null
                ? MediaType.APPLICATION_OCTET_STREAM
                : MediaType.parseMediaType(download.mimeType());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(download.filename() == null ? "document" : download.filename())
                        .build()
                        .toString())
                .body(download.resource());
    }
}
