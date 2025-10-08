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
public class BranchStatsResponse {
    private Long branchId;
    private String branchName;
    private Integer totalProducts;
    private Integer totalStock;
    private Integer lowStockItems;
    private Long todayInvoices;
    private BigDecimal todaySales;
    private Long activeUsers;
}

