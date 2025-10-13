package com.retail.management.service;

import com.retail.management.dto.request.search.ReportFilterRequest;
import com.retail.management.dto.response.report.*;

import java.util.List;

public interface ReportService {
    DailySalesReport generateDailySalesReport(Long branchId, java.time.LocalDate date);
    SalesReportResponse generateSalesReport(ReportFilterRequest filter);
    List<ProductPerformanceDTO> getTopSellingProducts(ReportFilterRequest filter, int limit);
    List<BranchPerformanceResponse> getBranchPerformance(ReportFilterRequest filter);
    TaxSummaryResponse getTaxSummary(ReportFilterRequest filter);
    List<DiscountSummaryDTO> getDiscountSummary(ReportFilterRequest filter);
    LowStockAlertResponse getLowStockAlert(Long branchId);
}