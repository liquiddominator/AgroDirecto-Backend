package com.agrodirecto.auth.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CarrierProfileRequest(
        @NotNull(message = "El tipo de transporte es obligatorio")
        TransportType transportType,

        @NotNull(message = "La capacidad de carga es obligatoria")
        @DecimalMin(value = "0.0", inclusive = false, message = "La capacidad de carga debe ser mayor a cero")
        BigDecimal loadCapacityKg,

        @NotNull(message = "La zona de operacion es obligatoria")
        OperationZone operationZone,

        @NotBlank(message = "El numero de licencia es obligatorio")
        @Size(max = 50, message = "El numero de licencia no puede superar 50 caracteres")
        String driverLicenseNumber,

        @NotBlank(message = "La placa del vehiculo es obligatoria")
        @Size(max = 20, message = "La placa del vehiculo no puede superar 20 caracteres")
        String vehiclePlate
) {
}
