package com.parkeasy.parking_lot_service.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ParkingLotRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @DecimalMin(value = "-90.00", message = "latitude must be greater than or equal to -90")
    @DecimalMax(value = "90.00", message = "longitude must be smaller than or equal to 90")
    private Double latitude;

    @DecimalMin(value = "-180.00", message = "latitude must be greater than or equal to -180")
    @DecimalMax(value = "180.00", message = "longitude must be smaller than or equal to 180")
    private Double longitude;

    @NotNull(message = "Total spots are required")
    private Integer totalSpots;

    @NotNull(message = "Available spots are required")
    private Integer availableSpots;

    @NotNull(message = "Mention price per hour")
    private BigDecimal pricePerHour;

    @NotNull(message = "Please upload the images")
    private List<String> images;
}
