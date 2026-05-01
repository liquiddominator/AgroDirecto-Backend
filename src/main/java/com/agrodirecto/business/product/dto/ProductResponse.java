package com.agrodirecto.business.product.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductResponse(
        Long id,
        Long producerUserId,
        String name,
        String description,
        String unitCode,
        BigDecimal availableQuantity,
        BigDecimal unitPrice,
        Boolean preSale,
        LocalDate estimatedAvailableDate,
        LocalDate harvestAvailableDate,
        String statusCode
) {
}
