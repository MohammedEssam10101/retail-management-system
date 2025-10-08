package com.retail.management.dto.response.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private BigDecimal todaySales;
    private BigDecimal weekSales;
    private BigDecimal monthSales;
    private Long todayInvoices;
    private Long weekInvoices;
    private Long monthInvoices;
    private Integer lowStockProducts;
    private Integer outOfStockProducts;
    private Long activeCustomers;
    private Long pendingReturns;
}