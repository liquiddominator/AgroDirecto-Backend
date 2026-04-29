package com.agrodirecto.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "producer_profiles", schema = "agro_directo")
public class ProducerProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "producer_type", nullable = false, length = 30)
    private String producerType;

    @Column(name = "farm_name", nullable = false, length = 150)
    private String farmName;

    @Column(nullable = false, length = 100)
    private String municipality;

    @Column(nullable = false, length = 100)
    private String province;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(name = "experience_years", nullable = false)
    private Integer experienceYears;

    @Column(name = "geo_latitude", precision = 10, scale = 7)
    private BigDecimal geoLatitude;

    @Column(name = "geo_longitude", precision = 10, scale = 7)
    private BigDecimal geoLongitude;

    @Column(name = "geolocation_completed", nullable = false)
    private Boolean geolocationCompleted = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ProducerProfile() {
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setProducerType(String producerType) {
        this.producerType = producerType;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public void setGeoLatitude(BigDecimal geoLatitude) {
        this.geoLatitude = geoLatitude;
    }

    public void setGeoLongitude(BigDecimal geoLongitude) {
        this.geoLongitude = geoLongitude;
    }

    public void setGeolocationCompleted(Boolean geolocationCompleted) {
        this.geolocationCompleted = geolocationCompleted;
    }
}
