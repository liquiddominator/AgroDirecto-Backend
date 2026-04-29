package com.agrodirecto.auth.service;

import com.agrodirecto.auth.dto.BuyerProfileRequest;
import com.agrodirecto.auth.dto.CarrierProfileRequest;
import com.agrodirecto.auth.dto.ProducerProfileRequest;
import com.agrodirecto.auth.dto.RegisterRequest;
import com.agrodirecto.auth.dto.RegisterRole;
import com.agrodirecto.auth.dto.AuthResponse;
import com.agrodirecto.common.exception.BadRequestException;
import com.agrodirecto.common.exception.ResourceConflictException;
import com.agrodirecto.role.entity.Role;
import com.agrodirecto.role.repository.RoleRepository;
import com.agrodirecto.user.entity.BuyerProfile;
import com.agrodirecto.user.entity.CarrierProfile;
import com.agrodirecto.user.entity.ProducerProfile;
import com.agrodirecto.user.entity.User;
import com.agrodirecto.user.entity.UserRole;
import com.agrodirecto.user.entity.UserStatus;
import com.agrodirecto.user.repository.BuyerProfileRepository;
import com.agrodirecto.user.repository.CarrierProfileRepository;
import com.agrodirecto.user.repository.ProducerProfileRepository;
import com.agrodirecto.user.repository.UserRepository;
import com.agrodirecto.user.repository.UserRoleRepository;
import com.agrodirecto.user.repository.UserStatusRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthRegistrationService {

    private static final String INITIAL_USER_STATUS = "REGISTERED";

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final ProducerProfileRepository producerProfileRepository;
    private final BuyerProfileRepository buyerProfileRepository;
    private final CarrierProfileRepository carrierProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;

    public AuthRegistrationService(
            UserRepository userRepository,
            UserStatusRepository userStatusRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            ProducerProfileRepository producerProfileRepository,
            BuyerProfileRepository buyerProfileRepository,
            CarrierProfileRepository carrierProfileRepository,
            PasswordEncoder passwordEncoder,
            AuthTokenService authTokenService
    ) {
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.producerProfileRepository = producerProfileRepository;
        this.buyerProfileRepository = buyerProfileRepository;
        this.carrierProfileRepository = carrierProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authTokenService = authTokenService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletRequest servletRequest) {
        validateProfileSelection(request);

        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResourceConflictException("Ya existe un usuario registrado con ese email");
        }

        UserStatus status = userStatusRepository.findByCode(INITIAL_USER_STATUS)
                .orElseThrow(() -> new IllegalStateException("No existe el estado inicial de usuario: " + INITIAL_USER_STATUS));
        Role role = roleRepository.findByCode(request.role().name())
                .orElseThrow(() -> new IllegalStateException("No existe el rol de registro: " + request.role().name()));

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone().trim());
        user.setStatus(status);
        user.setAcceptedTerms(Boolean.TRUE);
        user.setAcceptedPrivacyPolicy(Boolean.TRUE);
        user.setEmailVerified(Boolean.FALSE);
        user.setPhoneVerified(Boolean.FALSE);
        user.setFailedLoginAttempts(0);

        User savedUser = userRepository.save(user);
        userRoleRepository.save(new UserRole(savedUser, role, true));
        createRoleProfile(savedUser, request);

        return authTokenService.issueTokenPair(savedUser, servletRequest);
    }

    private void validateProfileSelection(RegisterRequest request) {
        int suppliedProfiles = 0;
        if (request.producerProfile() != null) {
            suppliedProfiles++;
        }
        if (request.buyerProfile() != null) {
            suppliedProfiles++;
        }
        if (request.carrierProfile() != null) {
            suppliedProfiles++;
        }

        if (suppliedProfiles != 1) {
            throw new BadRequestException("Debe enviar exactamente un perfil segun el rol elegido");
        }

        if (request.role() == RegisterRole.PRODUCER && request.producerProfile() == null) {
            throw new BadRequestException("El registro como productor requiere producerProfile");
        }
        if (request.role() == RegisterRole.BUYER && request.buyerProfile() == null) {
            throw new BadRequestException("El registro como comprador requiere buyerProfile");
        }
        if (request.role() == RegisterRole.CARRIER && request.carrierProfile() == null) {
            throw new BadRequestException("El registro como transportista requiere carrierProfile");
        }
    }

    private String createRoleProfile(User user, RegisterRequest request) {
        return switch (request.role()) {
            case PRODUCER -> {
                createProducerProfile(user.getId(), request.producerProfile());
                yield "producerProfile";
            }
            case BUYER -> {
                createBuyerProfile(user.getId(), request.buyerProfile());
                yield "buyerProfile";
            }
            case CARRIER -> {
                createCarrierProfile(user.getId(), request.carrierProfile());
                yield "carrierProfile";
            }
        };
    }

    private void createProducerProfile(Long userId, ProducerProfileRequest request) {
        validateCoordinatePair(request.geoLatitude(), request.geoLongitude());

        ProducerProfile profile = new ProducerProfile();
        profile.setUserId(userId);
        profile.setProducerType(request.producerType().name());
        profile.setFarmName(request.farmName().trim());
        profile.setMunicipality(request.municipality().trim());
        profile.setProvince(request.province().trim());
        profile.setDepartment(request.department().trim());
        profile.setExperienceYears(request.experienceYears());
        profile.setGeoLatitude(request.geoLatitude());
        profile.setGeoLongitude(request.geoLongitude());
        profile.setGeolocationCompleted(request.geoLatitude() != null && request.geoLongitude() != null);
        producerProfileRepository.save(profile);
    }

    private void createBuyerProfile(Long userId, BuyerProfileRequest request) {
        BuyerProfile profile = new BuyerProfile();
        profile.setUserId(userId);
        profile.setBuyerType(request.buyerType().name());
        profile.setBusinessName(trimToNull(request.businessName()));
        profile.setCityOrPurchaseZone(request.cityOrPurchaseZone().trim());
        buyerProfileRepository.save(profile);
    }

    private void createCarrierProfile(Long userId, CarrierProfileRequest request) {
        CarrierProfile profile = new CarrierProfile();
        profile.setUserId(userId);
        profile.setTransportType(request.transportType().name());
        profile.setLoadCapacityKg(request.loadCapacityKg());
        profile.setOperationZone(request.operationZone().name());
        profile.setDriverLicenseNumber(request.driverLicenseNumber().trim());
        profile.setVehiclePlate(request.vehiclePlate().trim().toUpperCase(Locale.ROOT));
        carrierProfileRepository.save(profile);
    }

    private void validateCoordinatePair(Object latitude, Object longitude) {
        if ((latitude == null && longitude != null) || (latitude != null && longitude == null)) {
            throw new BadRequestException("La latitud y longitud deben enviarse juntas");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
