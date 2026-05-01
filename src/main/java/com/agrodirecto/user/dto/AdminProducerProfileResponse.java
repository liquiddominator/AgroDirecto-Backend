package com.agrodirecto.user.dto;

import java.math.BigDecimal;

public record AdminProducerProfileResponse(
        String producerType,
        String farmName,
        String municipality,
        String province,
        String department,
        Integer experienceYears,
        BigDecimal geoLatitude,
        BigDecimal geoLongitude,
        Boolean geolocationCompleted
) {
}
