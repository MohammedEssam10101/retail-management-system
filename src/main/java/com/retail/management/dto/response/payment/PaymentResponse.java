package com.retail.management.dto.response.payment;

import com.retail.management.enums.PaymentMethod;
import com.retail.management.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long invoiceId;
    private String invoiceNumber;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private String referenceNumber;
    private PaymentStatus status;
    private String notes;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private String createdBy;
}