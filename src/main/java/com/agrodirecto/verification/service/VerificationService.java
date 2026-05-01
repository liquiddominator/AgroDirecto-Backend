package com.agrodirecto.verification.service;

import com.agrodirecto.common.exception.BadRequestException;
import com.agrodirecto.common.exception.ForbiddenException;
import com.agrodirecto.common.exception.ResourceNotFoundException;
import com.agrodirecto.common.exception.UnauthorizedException;
import com.agrodirecto.security.jwt.AuthenticatedUser;
import com.agrodirecto.user.entity.User;
import com.agrodirecto.user.entity.UserStatus;
import com.agrodirecto.user.repository.UserRepository;
import com.agrodirecto.user.repository.UserStatusRepository;
import com.agrodirecto.verification.dto.ReviewDecision;
import com.agrodirecto.verification.dto.ReviewDecisionRequest;
import com.agrodirecto.verification.dto.VerificationDocumentResponse;
import com.agrodirecto.verification.dto.VerificationReviewResponse;
import com.agrodirecto.verification.entity.DocumentType;
import com.agrodirecto.verification.entity.VerificationDocument;
import com.agrodirecto.verification.entity.VerificationReview;
import com.agrodirecto.verification.repository.DocumentTypeRepository;
import com.agrodirecto.verification.repository.VerificationDocumentRepository;
import com.agrodirecto.verification.repository.VerificationReviewRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VerificationService {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String PENDING_STATUS = "PENDING_VERIFICATION";
    private static final String VERIFIED_STATUS = "VERIFIED";
    private static final String REJECTED_STATUS = "REJECTED";
    private static final Set<String> DOCUMENT_UPLOAD_STATUSES = Set.of("REGISTERED", "INCOMPLETE", PENDING_STATUS, REJECTED_STATUS);

    private final VerificationDocumentRepository documentRepository;
    private final VerificationReviewRepository reviewRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final VerificationStorageService storageService;

    public VerificationService(
            VerificationDocumentRepository documentRepository,
            VerificationReviewRepository reviewRepository,
            DocumentTypeRepository documentTypeRepository,
            UserRepository userRepository,
            UserStatusRepository userStatusRepository,
            VerificationStorageService storageService
    ) {
        this.documentRepository = documentRepository;
        this.reviewRepository = reviewRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
        this.storageService = storageService;
    }

    @Transactional
    public VerificationDocumentResponse uploadCurrentUserDocument(
            String documentTypeCode,
            String documentNumber,
            MultipartFile file
    ) {
        AuthenticatedUser principal = currentUser();
        User user = userRepository.findById(principal.id())
                .orElseThrow(() -> new ResourceNotFoundException("El usuario autenticado no existe"));

        if (!DOCUMENT_UPLOAD_STATUSES.contains(user.getStatus().getCode())) {
            throw new ForbiddenException("El usuario no se encuentra habilitado para cargar documentos de verificacion");
        }

        DocumentType documentType = documentTypeRepository.findByCode(documentTypeCode)
                .orElseThrow(() -> new ResourceNotFoundException("El tipo de documento no existe"));
        VerificationStorageService.StoredFile storedFile = storageService.store(file, user.getId());

        VerificationDocument document = new VerificationDocument();
        document.setUser(user);
        document.setDocumentType(documentType);
        document.setDocumentNumber(trimToNull(documentNumber));
        document.setFileUrl(storedFile.fileUrl());
        document.setOriginalFilename(storedFile.originalFilename());
        document.setMimeType(storedFile.mimeType());

        user.setStatus(status(PENDING_STATUS));
        userRepository.save(user);

        return toResponse(documentRepository.save(document), null);
    }

    @Transactional(readOnly = true)
    public List<VerificationDocumentResponse> listCurrentUserDocuments() {
        AuthenticatedUser principal = currentUser();
        List<VerificationDocument> documents = documentRepository.findAllWithDetailsByUserId(principal.id());
        Map<Long, VerificationReview> latestReviews = latestReviewsByDocumentId(documents);
        return documents.stream()
                .map(document -> toResponse(document, latestReviews.get(document.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VerificationDocumentResponse> listVerificationRequests() {
        requireAdmin();
        List<VerificationDocument> documents = documentRepository.findAllWithDetails();
        Map<Long, VerificationReview> latestReviews = latestReviewsByDocumentId(documents);
        return documents.stream()
                .map(document -> toResponse(document, latestReviews.get(document.getId())))
                .toList();
    }

    @Transactional
    public VerificationDocumentResponse reviewDocument(Long documentId, ReviewDecisionRequest request) {
        AuthenticatedUser admin = requireAdmin();
        VerificationDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("El documento de verificacion no existe"));
        User reviewer = userRepository.findById(admin.id())
                .orElseThrow(() -> new ResourceNotFoundException("El administrador autenticado no existe"));

        if (request.decision() == ReviewDecision.REJECTED && isBlank(request.reason())) {
            throw new BadRequestException("El motivo es obligatorio cuando se rechaza un documento");
        }

        VerificationReview review = new VerificationReview();
        review.setDocument(document);
        review.setReviewedBy(reviewer);
        review.setDecision(request.decision().name());
        review.setReason(trimToNull(request.reason()));
        review.setEvidenceUrl(trimToNull(request.evidenceUrl()));
        VerificationReview savedReview = reviewRepository.save(review);

        User reviewedUser = document.getUser();
        reviewedUser.setStatus(status(request.decision() == ReviewDecision.APPROVED ? VERIFIED_STATUS : REJECTED_STATUS));
        userRepository.save(reviewedUser);

        return toResponse(document, savedReview);
    }

    @Transactional(readOnly = true)
    public FileDownload loadDocumentFile(Long documentId) {
        AuthenticatedUser principal = currentUser();
        VerificationDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("El documento de verificacion no existe"));

        boolean owner = document.getUser().getId().equals(principal.id());
        boolean admin = principal.roles().contains(ADMIN_ROLE);
        if (!owner && !admin) {
            throw new ForbiddenException("No tiene permisos para acceder al documento");
        }

        return new FileDownload(
                storageService.load(document.getFileUrl()),
                document.getOriginalFilename(),
                document.getMimeType()
        );
    }

    private Map<Long, VerificationReview> latestReviewsByDocumentId(List<VerificationDocument> documents) {
        List<Long> documentIds = documents.stream().map(VerificationDocument::getId).toList();
        if (documentIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, VerificationReview> latestByDocumentId = new HashMap<>();
        reviewRepository.findAllLatestCandidatesByDocumentIds(documentIds).forEach(review ->
                latestByDocumentId.putIfAbsent(review.getDocument().getId(), review)
        );
        return latestByDocumentId;
    }

    private VerificationDocumentResponse toResponse(VerificationDocument document, VerificationReview latestReview) {
        User user = document.getUser();
        DocumentType type = document.getDocumentType();
        return new VerificationDocumentResponse(
                document.getId(),
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getStatus().getCode(),
                type.getCode(),
                type.getName(),
                document.getDocumentNumber(),
                document.getOriginalFilename(),
                document.getMimeType(),
                "/api/verification/documents/" + document.getId() + "/file",
                document.getUploadedAt(),
                latestReview == null ? null : toReviewResponse(latestReview)
        );
    }

    private VerificationReviewResponse toReviewResponse(VerificationReview review) {
        return new VerificationReviewResponse(
                review.getId(),
                review.getDecision(),
                review.getReason(),
                review.getEvidenceUrl(),
                review.getReviewedBy().getId(),
                review.getReviewedBy().getFullName(),
                review.getReviewedAt()
        );
    }

    private AuthenticatedUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new UnauthorizedException("No existe una sesion autenticada");
        }
        return user;
    }

    private AuthenticatedUser requireAdmin() {
        AuthenticatedUser user = currentUser();
        if (!user.roles().contains(ADMIN_ROLE)) {
            throw new ForbiddenException("La operacion requiere rol Administrador");
        }
        return user;
    }

    private UserStatus status(String code) {
        return userStatusRepository.findByCode(code)
                .orElseThrow(() -> new IllegalStateException("No existe el estado de usuario: " + code));
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public record FileDownload(Resource resource, String filename, String mimeType) {
    }
}
