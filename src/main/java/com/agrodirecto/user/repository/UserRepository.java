package com.agrodirecto.user.repository;

import com.agrodirecto.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    @org.springframework.data.jpa.repository.Query("""
            select u from User u
            join fetch u.status
            order by u.createdAt desc
            """)
    java.util.List<User> findAllWithStatus();

    @org.springframework.data.jpa.repository.Query("""
            select u from User u
            join fetch u.status
            where u.id = :id
            """)
    Optional<User> findByIdWithStatus(Long id);
}
