package com.agrodirecto.user.repository;

import com.agrodirecto.user.entity.BuyerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyerProfileRepository extends JpaRepository<BuyerProfile, Long> {
}
