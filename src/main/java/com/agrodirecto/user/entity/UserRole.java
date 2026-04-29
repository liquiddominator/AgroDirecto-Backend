package com.agrodirecto.user.entity;

import com.agrodirecto.role.entity.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_roles", schema = "agro_directo")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "is_primary", nullable = false)
    private Boolean primaryRole = false;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    protected UserRole() {
    }

    public UserRole(User user, Role role, boolean primaryRole) {
        this.user = user;
        this.role = role;
        this.primaryRole = primaryRole;
    }

    @PrePersist
    void prePersist() {
        assignedAt = LocalDateTime.now();
    }

    public Role getRole() {
        return role;
    }

    public Boolean getPrimaryRole() {
        return primaryRole;
    }
}
