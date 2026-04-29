package com.agrodirecto.user.repository;

import com.agrodirecto.user.entity.UserRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("select ur from UserRole ur join fetch ur.role where ur.user.id = :userId")
    List<UserRole> findAllWithRoleByUserId(@Param("userId") Long userId);

    @Query("select ur from UserRole ur join fetch ur.role where ur.user.id = :userId and ur.primaryRole = true")
    Optional<UserRole> findPrimaryWithRoleByUserId(@Param("userId") Long userId);
}
