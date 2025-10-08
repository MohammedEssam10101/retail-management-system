package com.retail.management.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchPerformanceResponse {
    private Long branchId;
    private String branchName;
    private String city;
    private Long invoiceCount;
    private BigDecimal totalSales;
    private BigDecimal averageOrderValue;
    private Integer rank;
}