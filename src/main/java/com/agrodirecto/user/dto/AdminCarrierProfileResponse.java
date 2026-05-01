package com.agrodirecto.user.dto;

import java.math.BigDecimal;

public record AdminCarrierProfileResponse(
        String transportType,
        BigDecimal loadCapacityKg,
        String operationZone,
        String driverLicenseNumber,
        String vehiclePlate
) {
}
