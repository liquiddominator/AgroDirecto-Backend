package com.agrodirecto.verification.service;

import com.agrodirecto.common.exception.ForbiddenException;
import com.agrodirecto.common.exception.ResourceNotFoundException;
import com.agrodirecto.user.entity.User;
import com.agrodirecto.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserVerificationGuard {

    private static final String VERIFIED_STATUS = "VERIFIED";

    private final UserRepository userRepository;

    public UserVerificationGuard(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public void requireVerifiedUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe"));

        if (!VERIFIED_STATUS.equals(user.getStatus().getCode())) {
            throw new ForbiddenException("Debe completar la verificacion de identidad para usar esta funcionalidad");
        }
    }
}
