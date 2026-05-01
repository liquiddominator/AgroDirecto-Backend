package com.agrodirecto.verification.repository;

import com.agrodirecto.verification.entity.VerificationDocument;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VerificationDocumentRepository extends JpaRepository<VerificationDocument, Long> {

    @Query("""
            select d from VerificationDocument d
            join fetch d.user u
            join fetch u.status
            join fetch d.documentType
            where u.id = :userId
            order by d.uploadedAt desc
            """)
    List<VerificationDocument> findAllWithDetailsByUserId(Long userId);

    @Query("""
            select d from VerificationDocument d
            join fetch d.user u
            join fetch u.status
            join fetch d.documentType
            order by d.uploadedAt desc
            """)
    List<VerificationDocument> findAllWithDetails();
}
