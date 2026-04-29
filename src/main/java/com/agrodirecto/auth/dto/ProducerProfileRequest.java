package com.agrodirecto.auth.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProducerProfileRequest(
        @NotNull(message = "El tipo de productor es obligatorio")
        ProducerType producerType,

        @NotBlank(message = "El nombre de la finca es obligatorio")
        @Size(max = 150, message = "El nombre de la finca no puede superar 150 caracteres")
        String farmName,

        @NotBlank(message = "El municipio es obligatorio")
        @Size(max = 100, message = "El municipio no puede superar 100 caracteres")
        String municipality,

        @NotBlank(message = "La provincia es obligatoria")
        @Size(max = 100, message = "La provincia no puede superar 100 caracteres")
        String province,

        @NotBlank(message = "El departamento es obligatorio")
        @Size(max = 100, message = "El departamento no puede superar 100 caracteres")
        String department,

        @NotNull(message = "Los anios de experiencia son obligatorios")
        @Min(value = 0, message = "Los anios de experiencia no pueden ser negativos")
        Integer experienceYears,

        @DecimalMin(value = "-90.0", message = "La latitud minima es -90")
        @DecimalMax(value = "90.0", message = "La latitud maxima es 90")
        BigDecimal geoLatitude,

        @DecimalMin(value = "-180.0", message = "La longitud minima es -180")
        @DecimalMax(value = "180.0", message = "La longitud maxima es 180")
        BigDecimal geoLongitude
) {
}
