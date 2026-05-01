package com.agrodirecto.business.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateProductRequest(
        @NotBlank(message = "El nombre del producto es obligatorio")
        @Size(max = 150, message = "El nombre del producto no puede superar 150 caracteres")
        String name,

        String description,

        @NotBlank(message = "La unidad del producto es obligatoria")
        @Size(max = 20, message = "La unidad del producto no puede superar 20 caracteres")
        String unitCode,

        @NotNull(message = "La cantidad disponible es obligatoria")
        @DecimalMin(value = "0.0", message = "La cantidad disponible no puede ser negativa")
        BigDecimal availableQuantity,

        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.0", message = "El precio unitario no puede ser negativo")
        BigDecimal unitPrice,

        Boolean preSale,

        LocalDate estimatedAvailableDate,

        LocalDate harvestAvailableDate
) {
}
