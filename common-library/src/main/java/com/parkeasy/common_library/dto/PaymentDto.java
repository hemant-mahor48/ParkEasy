package com.parkeasy.common_library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long id;

    private Long bookingId;

    private Double amount;

    private String paymentMethod; // CARD, UPI, NET_BANKING

    private String status; // SUCCESS, FAILED, PENDING

    private String transactionId;

    private String receiptUrl;
}
