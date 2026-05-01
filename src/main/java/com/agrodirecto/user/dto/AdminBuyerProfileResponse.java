package com.agrodirecto.user.dto;

public record AdminBuyerProfileResponse(
        String buyerType,
        String businessName,
        String cityOrPurchaseZone
) {
}
