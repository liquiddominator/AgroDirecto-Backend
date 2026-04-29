package com.agrodirecto.user.repository;

import com.agrodirecto.user.entity.ProducerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProducerProfileRepository extends JpaRepository<ProducerProfile, Long> {
}
