package com.agrodirecto.business.product.repository;

import com.agrodirecto.business.product.entity.ProductUnit;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductUnitRepository extends JpaRepository<ProductUnit, Long> {

    Optional<ProductUnit> findByCode(String code);
}
