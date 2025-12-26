package com.parkeasy.common_library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLotDto {
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    private Double latitude;

    private Double longitude;

    private Integer totalSpots;

    private Integer availableSpots;

    private Double pricePerHour;

    private String description;

    private Boolean active;
}
