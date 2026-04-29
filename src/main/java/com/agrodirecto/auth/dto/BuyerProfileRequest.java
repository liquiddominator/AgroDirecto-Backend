package com.agrodirecto.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BuyerProfileRequest(
        @NotNull(message = "El tipo de comprador es obligatorio")
        BuyerType buyerType,

        @Size(max = 150, message = "El nombre comercial no puede superar 150 caracteres")
        String businessName,

        @NotBlank(message = "La ciudad o zona de compra es obligatoria")
        @Size(max = 150, message = "La ciudad o zona de compra no puede superar 150 caracteres")
        String cityOrPurchaseZone
) {
}
