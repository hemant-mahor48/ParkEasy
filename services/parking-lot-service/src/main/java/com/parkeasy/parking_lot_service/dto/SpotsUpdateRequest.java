package com.parkeasy.parking_lot_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpotsUpdateRequest {

    @NotNull(message = "Change value is required")
    private Integer change;  // Positive or negative
}
