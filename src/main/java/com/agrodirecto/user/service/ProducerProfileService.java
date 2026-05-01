package com.agrodirecto.user.service;

import com.agrodirecto.common.exception.ForbiddenException;
import com.agrodirecto.common.exception.ResourceNotFoundException;
import com.agrodirecto.common.exception.UnauthorizedException;
import com.agrodirecto.security.jwt.AuthenticatedUser;
import com.agrodirecto.user.dto.ProducerProfileResponse;
import com.agrodirecto.user.dto.UpdateProducerLocationRequest;
import com.agrodirecto.user.entity.ProducerProfile;
import com.agrodirecto.user.repository.ProducerProfileRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProducerProfileService {

    private static final String PRODUCER_ROLE = "PRODUCER";

    private final ProducerProfileRepository producerProfileRepository;

    public ProducerProfileService(ProducerProfileRepository producerProfileRepository) {
        this.producerProfileRepository = producerProfileRepository;
    }

    @Transactional(readOnly = true)
    public ProducerProfileResponse getCurrentProducerProfile() {
        AuthenticatedUser user = currentProducer();
        return toResponse(findProfile(user.id()));
    }

    @Transactional
    public ProducerProfileResponse updateCurrentProducerLocation(UpdateProducerLocationRequest request) {
        AuthenticatedUser user = currentProducer();
        ProducerProfile profile = findProfile(user.id());
        profile.setGeoLatitude(request.geoLatitude());
        profile.setGeoLongitude(request.geoLongitude());
        profile.setGeolocationCompleted(Boolean.TRUE);
        return toResponse(producerProfileRepository.save(profile));
    }

    @Transactional(readOnly = true)
    public void requireCompletedGeolocation(Long producerUserId) {
        if (!producerProfileRepository.existsByUserIdAndGeolocationCompletedTrue(producerUserId)) {
            throw new ForbiddenException("Debe registrar la ubicacion GPS de su finca antes de publicar productos");
        }
    }

    private ProducerProfile findProfile(Long userId) {
        return producerProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe perfil de productor para el usuario autenticado"));
    }

    private AuthenticatedUser currentProducer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new UnauthorizedException("No existe una sesion autenticada");
        }
        if (!user.roles().contains(PRODUCER_ROLE)) {
            throw new ForbiddenException("La operacion requiere rol Productor");
        }
        return user;
    }

    private ProducerProfileResponse toResponse(ProducerProfile profile) {
        return new ProducerProfileResponse(
                profile.getUserId(),
                profile.getProducerType(),
                profile.getFarmName(),
                profile.getMunicipality(),
                profile.getProvince(),
                profile.getDepartment(),
                profile.getExperienceYears(),
                profile.getGeoLatitude(),
                profile.getGeoLongitude(),
                profile.getGeolocationCompleted()
        );
    }
}
