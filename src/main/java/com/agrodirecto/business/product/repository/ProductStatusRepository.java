package com.agrodirecto.business.product.repository;

import com.agrodirecto.business.product.entity.ProductStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStatusRepository extends JpaRepository<ProductStatus, Long> {

    Optional<ProductStatus> findByCode(String code);
}
