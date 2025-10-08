package com.retail.management.dto.request.search;

import com.retail.management.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceFilterRequest {
    private Long branchId;
    private Long customerId;
    private Long cashierId;
    private InvoiceStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String customerType;
    private Boolean hasOutstandingBalance;
}