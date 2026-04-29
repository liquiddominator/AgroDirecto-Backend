package com.agrodirecto.auth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 150, message = "El nombre completo no puede superar 150 caracteres")
        String fullName,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato valido")
        @Size(max = 150, message = "El email no puede superar 150 caracteres")
        String email,

        @NotBlank(message = "La contrasenia es obligatoria")
        @Size(min = 6, max = 72, message = "La contrasenia debe tener entre 6 y 72 caracteres")
        String password,

        @NotBlank(message = "El telefono es obligatorio")
        @Size(max = 30, message = "El telefono no puede superar 30 caracteres")
        String phone,

        @NotNull(message = "El rol de registro es obligatorio")
        RegisterRole role,

        @NotNull(message = "Debe aceptar los terminos")
        @AssertTrue(message = "Debe aceptar los terminos")
        Boolean acceptedTerms,

        @NotNull(message = "Debe aceptar la politica de privacidad")
        @AssertTrue(message = "Debe aceptar la politica de privacidad")
        Boolean acceptedPrivacyPolicy,

        @Valid
        ProducerProfileRequest producerProfile,

        @Valid
        BuyerProfileRequest buyerProfile,

        @Valid
        CarrierProfileRequest carrierProfile
) {
}
