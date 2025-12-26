package com.parkeasy.common_library.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent {
    private Long bookingId;
    private Long userId;
    private String parkingLotId;
    private String eventType; // BOOKING_CREATED, BOOKING_CONFIRMED, BOOKING_CANCELLED
    private Long timestamp;
    private String userEmail;
    private String parkingLotName;
}