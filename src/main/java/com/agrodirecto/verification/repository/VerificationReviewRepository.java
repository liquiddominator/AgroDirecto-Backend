package com.agrodirecto.verification.repository;

import com.agrodirecto.verification.entity.VerificationReview;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VerificationReviewRepository extends JpaRepository<VerificationReview, Long> {

    @Query("""
            select r from VerificationReview r
            join fetch r.reviewedBy
            where r.document.id in :documentIds
            order by r.reviewedAt desc
            """)
    List<VerificationReview> findAllLatestCandidatesByDocumentIds(Collection<Long> documentIds);

    Optional<VerificationReview> findFirstByDocumentIdOrderByReviewedAtDesc(Long documentId);
}
