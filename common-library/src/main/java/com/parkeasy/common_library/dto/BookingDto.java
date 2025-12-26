package com.parkeasy.common_library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;

    private Long userId;

    private String parkingLotId;

    private Integer spotNumber;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status; // PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED

    private Double totalAmount;

    private String paymentStatus; // PENDING, SUCCESS, FAILED, REFUNDED
}
