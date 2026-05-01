package com.agrodirecto.business.product.service;

import com.agrodirecto.business.product.dto.CreateProductRequest;
import com.agrodirecto.business.product.dto.ProductResponse;
import com.agrodirecto.business.product.entity.Product;
import com.agrodirecto.business.product.entity.ProductStatus;
import com.agrodirecto.business.product.entity.ProductUnit;
import com.agrodirecto.business.product.repository.ProductRepository;
import com.agrodirecto.business.product.repository.ProductStatusRepository;
import com.agrodirecto.business.product.repository.ProductUnitRepository;
import com.agrodirecto.common.exception.BadRequestException;
import com.agrodirecto.common.exception.ForbiddenException;
import com.agrodirecto.common.exception.ResourceNotFoundException;
import com.agrodirecto.common.exception.UnauthorizedException;
import com.agrodirecto.security.jwt.AuthenticatedUser;
import com.agrodirecto.user.service.ProducerProfileService;
import com.agrodirecto.verification.service.UserVerificationGuard;
import java.util.Locale;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private static final String PRODUCER_ROLE = "PRODUCER";
    private static final String PUBLISHED_STATUS = "PUBLISHED";

    private final ProductRepository productRepository;
    private final ProductUnitRepository productUnitRepository;
    private final ProductStatusRepository productStatusRepository;
    private final ProducerProfileService producerProfileService;
    private final UserVerificationGuard userVerificationGuard;

    public ProductService(
            ProductRepository productRepository,
            ProductUnitRepository productUnitRepository,
            ProductStatusRepository productStatusRepository,
            ProducerProfileService producerProfileService,
            UserVerificationGuard userVerificationGuard
    ) {
        this.productRepository = productRepository;
        this.productUnitRepository = productUnitRepository;
        this.productStatusRepository = productStatusRepository;
        this.producerProfileService = producerProfileService;
        this.userVerificationGuard = userVerificationGuard;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        AuthenticatedUser producer = currentProducer();
        userVerificationGuard.requireVerifiedUser(producer.id());
        producerProfileService.requireCompletedGeolocation(producer.id());

        boolean preSale = Boolean.TRUE.equals(request.preSale());
        if (preSale && request.estimatedAvailableDate() == null) {
            throw new BadRequestException("La fecha estimada de disponibilidad es obligatoria para preventa");
        }

        ProductUnit unit = productUnitRepository.findByCode(request.unitCode().trim().toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new ResourceNotFoundException("La unidad de producto no existe"));
        ProductStatus status = productStatusRepository.findByCode(PUBLISHED_STATUS)
                .orElseThrow(() -> new IllegalStateException("No existe el estado de producto: " + PUBLISHED_STATUS));

        Product product = new Product();
        product.setProducerUserId(producer.id());
        product.setName(request.name().trim());
        product.setDescription(trimToNull(request.description()));
        product.setUnit(unit);
        product.setAvailableQuantity(request.availableQuantity());
        product.setUnitPrice(request.unitPrice());
        product.setPreSale(preSale);
        product.setEstimatedAvailableDate(request.estimatedAvailableDate());
        product.setHarvestAvailableDate(request.harvestAvailableDate());
        product.setStatus(status);

        return toResponse(productRepository.save(product));
    }

    private AuthenticatedUser currentProducer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new UnauthorizedException("No existe una sesion autenticada");
        }
        if (!user.roles().contains(PRODUCER_ROLE)) {
            throw new ForbiddenException("La operacion requiere rol Productor");
        }
        return user;
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getProducerUserId(),
                product.getName(),
                product.getDescription(),
                product.getUnit().getCode(),
                product.getAvailableQuantity(),
                product.getUnitPrice(),
                product.getPreSale(),
                product.getEstimatedAvailableDate(),
                product.getHarvestAvailableDate(),
                product.getStatus().getCode()
        );
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
