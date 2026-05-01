package com.agrodirecto.user.controller;

import com.agrodirecto.user.dto.ProducerProfileResponse;
import com.agrodirecto.user.dto.UpdateProducerLocationRequest;
import com.agrodirecto.user.service.ProducerProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/producers/me")
public class ProducerProfileController {

    private final ProducerProfileService producerProfileService;

    public ProducerProfileController(ProducerProfileService producerProfileService) {
        this.producerProfileService = producerProfileService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ProducerProfileResponse> profile() {
        return ResponseEntity.ok(producerProfileService.getCurrentProducerProfile());
    }

    @PatchMapping("/location")
    public ResponseEntity<ProducerProfileResponse> updateLocation(
            @Valid @RequestBody UpdateProducerLocationRequest request
    ) {
        return ResponseEntity.ok(producerProfileService.updateCurrentProducerLocation(request));
    }
}
