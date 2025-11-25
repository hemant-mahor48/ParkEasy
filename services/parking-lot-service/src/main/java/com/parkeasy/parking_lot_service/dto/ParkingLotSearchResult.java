package com.parkeasy.parking_lot_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingLotSearchResult {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Integer availableSpots;
    private BigDecimal pricePerHour;
    private Double distanceKm;
    private String mainImage;  // First image from list
    private String status;
}
