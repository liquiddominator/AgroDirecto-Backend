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
@Table(name = "carrier_profiles", schema = "agro_directo")
public class CarrierProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "transport_type", nullable = false, length = 30)
    private String transportType;

    @Column(name = "load_capacity_kg", nullable = false, precision = 12, scale = 2)
    private BigDecimal loadCapacityKg;

    @Column(name = "operation_zone", nullable = false, length = 50)
    private String operationZone;

    @Column(name = "driver_license_number", nullable = false, length = 50)
    private String driverLicenseNumber;

    @Column(name = "vehicle_plate", nullable = false, length = 20)
    private String vehiclePlate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public CarrierProfile() {
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

    public Long getUserId() {
        return userId;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public BigDecimal getLoadCapacityKg() {
        return loadCapacityKg;
    }

    public void setLoadCapacityKg(BigDecimal loadCapacityKg) {
        this.loadCapacityKg = loadCapacityKg;
    }

    public String getOperationZone() {
        return operationZone;
    }

    public void setOperationZone(String operationZone) {
        this.operationZone = operationZone;
    }

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }
}
