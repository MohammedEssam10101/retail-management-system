package com.retail.management.controller;

import com.retail.management.dto.request.search.ReportFilterRequest;
import com.retail.management.dto.response.ApiResponse;
import com.retail.management.dto.response.report.*;
import com.retail.management.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "APIs for generating various business reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/daily-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Daily sales report", description = "Generate daily sales report for a branch")
    public ResponseEntity<ApiResponse<DailySalesReport>> getDailySalesReport(
            @RequestParam Long branchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailySalesReport report = reportService.generateDailySalesReport(branchId, date);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @PostMapping("/sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Sales report", description = "Generate sales report with filters")
    public ResponseEntity<ApiResponse<SalesReportResponse>> getSalesReport(
            @Valid @RequestBody ReportFilterRequest filter) {
        SalesReportResponse report = reportService.generateSalesReport(filter);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @PostMapping("/top-products")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Top selling products", description = "Get top selling products")
    public ResponseEntity<ApiResponse<List<ProductPerformanceDTO>>> getTopSellingProducts(
            @Valid @RequestBody ReportFilterRequest filter,
            @RequestParam(defaultValue = "10") int limit) {
        List<ProductPerformanceDTO> products = reportService.getTopSellingProducts(filter, limit);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @PostMapping("/branch-performance")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Branch performance", description = "Get branch performance comparison")
    public ResponseEntity<ApiResponse<List<BranchPerformanceResponse>>> getBranchPerformance(
            @Valid @RequestBody ReportFilterRequest filter) {
        List<BranchPerformanceResponse> performance = reportService.getBranchPerformance(filter);
        return ResponseEntity.ok(ApiResponse.success(performance));
    }

    @PostMapping("/tax-summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Tax summary", description = "Generate tax collection summary")
    public ResponseEntity<ApiResponse<TaxSummaryResponse>> getTaxSummary(
            @Valid @RequestBody ReportFilterRequest filter) {
        TaxSummaryResponse summary = reportService.getTaxSummary(filter);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @PostMapping("/discount-summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Discount summary", description = "Generate discount usage summary")
    public ResponseEntity<ApiResponse<List<DiscountSummaryDTO>>> getDiscountSummary(
            @Valid @RequestBody ReportFilterRequest filter) {
        List<DiscountSummaryDTO> summary = reportService.getDiscountSummary(filter);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/low-stock-alert/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Low stock alert", description = "Get low stock alert for a branch")
    public ResponseEntity<ApiResponse<LowStockAlertResponse>> getLowStockAlert(@PathVariable Long branchId) {
        LowStockAlertResponse alert = reportService.getLowStockAlert(branchId);
        return ResponseEntity.ok(ApiResponse.success(alert));
    }
}