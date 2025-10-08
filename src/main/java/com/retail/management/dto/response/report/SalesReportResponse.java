package com.retail.management.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesReportResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalSales;
    private BigDecimal totalTax;
    private BigDecimal totalDiscount;
    private BigDecimal netRevenue;
    private Long totalInvoices;
    private Long totalCustomers;
    private Map<String, BigDecimal> salesByBranch;
    private Map<String, BigDecimal> salesByPaymentMethod;
}