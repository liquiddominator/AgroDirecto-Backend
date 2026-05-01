package com.agrodirecto.user.service;

import com.agrodirecto.common.exception.ForbiddenException;
import com.agrodirecto.common.exception.ResourceNotFoundException;
import com.agrodirecto.common.exception.UnauthorizedException;
import com.agrodirecto.security.jwt.AuthenticatedUser;
import com.agrodirecto.user.dto.AdminBuyerProfileResponse;
import com.agrodirecto.user.dto.AdminCarrierProfileResponse;
import com.agrodirecto.user.dto.AdminProducerProfileResponse;
import com.agrodirecto.user.dto.AdminUserDetailResponse;
import com.agrodirecto.user.dto.AdminUserSummaryResponse;
import com.agrodirecto.user.entity.BuyerProfile;
import com.agrodirecto.user.entity.CarrierProfile;
import com.agrodirecto.user.entity.ProducerProfile;
import com.agrodirecto.user.entity.User;
import com.agrodirecto.user.entity.UserRole;
import com.agrodirecto.user.repository.BuyerProfileRepository;
import com.agrodirecto.user.repository.CarrierProfileRepository;
import com.agrodirecto.user.repository.ProducerProfileRepository;
import com.agrodirecto.user.repository.UserRepository;
import com.agrodirecto.user.repository.UserRoleRepository;
import com.agrodirecto.verification.dto.VerificationDocumentResponse;
import com.agrodirecto.verification.entity.VerificationDocument;
import com.agrodirecto.verification.entity.VerificationReview;
import com.agrodirecto.verification.repository.VerificationDocumentRepository;
import com.agrodirecto.verification.repository.VerificationReviewRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserService {

    private static final String ADMIN_ROLE = "ADMIN";

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ProducerProfileRepository producerProfileRepository;
    private final BuyerProfileRepository buyerProfileRepository;
    private final CarrierProfileRepository carrierProfileRepository;
    private final VerificationDocumentRepository documentRepository;
    private final VerificationReviewRepository reviewRepository;

    public AdminUserService(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            ProducerProfileRepository producerProfileRepository,
            BuyerProfileRepository buyerProfileRepository,
            CarrierProfileRepository carrierProfileRepository,
            VerificationDocumentRepository documentRepository,
            VerificationReviewRepository reviewRepository
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.producerProfileRepository = producerProfileRepository;
        this.buyerProfileRepository = buyerProfileRepository;
        this.carrierProfileRepository = carrierProfileRepository;
        this.documentRepository = documentRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminUserSummaryResponse> listUsers() {
        requireAdmin();
        return userRepository.findAllWithStatus().stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminUserDetailResponse getUserDetail(Long userId) {
        requireAdmin();
        User user = userRepository.findByIdWithStatus(userId)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe"));
        List<String> roles = roles(user.getId());
        List<VerificationDocument> documents = documentRepository.findAllWithDetailsByUserId(user.getId());
        Map<Long, VerificationReview> latestReviews = latestReviewsByDocumentId(documents);

        List<VerificationDocumentResponse> documentResponses = documents.stream()
                .map(document -> toDocumentResponse(document, latestReviews.get(document.getId())))
                .toList();

        return new AdminUserDetailResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus().getCode(),
                primaryRole(user.getId(), roles),
                roles,
                user.getAcceptedTerms(),
                user.getAcceptedPrivacyPolicy(),
                user.getEmailVerified(),
                user.getPhoneVerified(),
                profileFor(user.getId(), roles),
                documentResponses
        );
    }

    private AdminUserSummaryResponse toSummary(User user) {
        List<String> roles = roles(user.getId());
        return new AdminUserSummaryResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus().getCode(),
                primaryRole(user.getId(), roles),
                roles,
                user.getEmailVerified(),
                user.getPhoneVerified()
        );
    }

    private Object profileFor(Long userId, List<String> roles) {
        if (roles.contains("PRODUCER")) {
            return producerProfileRepository.findById(userId)
                    .map(this::toProducerProfile)
                    .orElse(null);
        }
        if (roles.contains("BUYER")) {
            return buyerProfileRepository.findById(userId)
                    .map(this::toBuyerProfile)
                    .orElse(null);
        }
        if (roles.contains("CARRIER")) {
            return carrierProfileRepository.findById(userId)
                    .map(this::toCarrierProfile)
                    .orElse(null);
        }
        return null;
    }

    private AdminProducerProfileResponse toProducerProfile(ProducerProfile profile) {
        return new AdminProducerProfileResponse(
                profile.getProducerType(),
                profile.getFarmName(),
                profile.getMunicipality(),
                profile.getProvince(),
                profile.getDepartment(),
                profile.getExperienceYears(),
                profile.getGeoLatitude(),
                profile.getGeoLongitude(),
                profile.getGeolocationCompleted()
        );
    }

    private AdminBuyerProfileResponse toBuyerProfile(BuyerProfile profile) {
        return new AdminBuyerProfileResponse(
                profile.getBuyerType(),
                profile.getBusinessName(),
                profile.getCityOrPurchaseZone()
        );
    }

    private AdminCarrierProfileResponse toCarrierProfile(CarrierProfile profile) {
        return new AdminCarrierProfileResponse(
                profile.getTransportType(),
                profile.getLoadCapacityKg(),
                profile.getOperationZone(),
                profile.getDriverLicenseNumber(),
                profile.getVehiclePlate()
        );
    }

    private List<String> roles(Long userId) {
        return userRoleRepository.findAllWithRoleByUserId(userId).stream()
                .map(UserRole::getRole)
                .map(role -> role.getCode())
                .toList();
    }

    private String primaryRole(Long userId, List<String> roles) {
        return userRoleRepository.findPrimaryWithRoleByUserId(userId)
                .map(UserRole::getRole)
                .map(role -> role.getCode())
                .orElse(roles.isEmpty() ? null : roles.getFirst());
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

    private VerificationDocumentResponse toDocumentResponse(VerificationDocument document, VerificationReview review) {
        return new VerificationDocumentResponse(
                document.getId(),
                document.getUser().getId(),
                document.getUser().getFullName(),
                document.getUser().getEmail(),
                document.getUser().getStatus().getCode(),
                document.getDocumentType().getCode(),
                document.getDocumentType().getName(),
                document.getDocumentNumber(),
                document.getOriginalFilename(),
                document.getMimeType(),
                "/api/verification/documents/" + document.getId() + "/file",
                document.getUploadedAt(),
                review == null ? null : new com.agrodirecto.verification.dto.VerificationReviewResponse(
                        review.getId(),
                        review.getDecision(),
                        review.getReason(),
                        review.getEvidenceUrl(),
                        review.getReviewedBy().getId(),
                        review.getReviewedBy().getFullName(),
                        review.getReviewedAt()
                )
        );
    }

    private AuthenticatedUser requireAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new UnauthorizedException("No existe una sesion autenticada");
        }
        if (!user.roles().contains(ADMIN_ROLE)) {
            throw new ForbiddenException("La operacion requiere rol Administrador");
        }
        return user;
    }
}
