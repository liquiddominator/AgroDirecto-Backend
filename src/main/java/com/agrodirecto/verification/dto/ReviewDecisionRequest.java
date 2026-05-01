package com.agrodirecto.verification.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewDecisionRequest(
        @NotNull(message = "La decision es obligatoria")
        ReviewDecision decision,

        @Size(max = 2000, message = "El motivo no puede superar 2000 caracteres")
        String reason,

        String evidenceUrl
) {
}
