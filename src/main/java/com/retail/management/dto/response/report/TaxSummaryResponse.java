package com.retail.management.dto.response.report;

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
public class TaxSummaryResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalTaxCollected;
    private BigDecimal totalSalesBeforeTax;
    private BigDecimal totalSalesAfterTax;
    private BigDecimal averageTaxRate;
}