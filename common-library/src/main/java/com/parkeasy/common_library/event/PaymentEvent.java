package com.parkeasy.common_library.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private Long paymentId;
    private Long bookingId;
    private Double amount;
    private String status; // SUCCESS, FAILED
    private String transactionId;
    private Long timestamp;
}