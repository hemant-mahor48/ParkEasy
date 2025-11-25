package com.parkeasy.parking_lot_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "parking_lots", indexes = {
        @Index(name = "idx_owner_id", columnList = "owner_id"),
        @Index(name = "idx_location", columnList = "latitude, longitude"),
        @Index(name = "idx_status", columnList = "status")
})
public class ParkingLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ownerId;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Integer totalSpots;
    private Integer availableSpots;
    private BigDecimal pricePerHour;

    @Enumerated(EnumType.STRING)
    private ParkingLotStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> images;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
