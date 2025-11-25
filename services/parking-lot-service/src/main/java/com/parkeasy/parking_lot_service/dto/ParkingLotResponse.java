package com.parkeasy.parking_lot_service.dto;

import com.parkeasy.parking_lot_service.model.ParkingLotStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ParkingLotResponse {
    private Long id;
    private Long ownerId;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Integer totalSpots;
    private Integer availableSpots;
    private BigDecimal pricePerHour;
    private ParkingLotStatus status;
    private List<String> images;
    private LocalDateTime createdAt;
}
