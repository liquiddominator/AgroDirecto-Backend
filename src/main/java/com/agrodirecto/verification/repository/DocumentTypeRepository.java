package com.agrodirecto.verification.repository;

import com.agrodirecto.verification.entity.DocumentType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {

    Optional<DocumentType> findByCode(String code);
}
