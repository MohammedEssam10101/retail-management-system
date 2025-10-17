package com.retail.management.controller;

import com.retail.management.dto.response.ApiResponse;
import com.retail.management.dto.response.stats.BranchStatsResponse;
import com.retail.management.dto.response.stats.DashboardStatsResponse;
import com.retail.management.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "APIs for dashboard statistics and metrics")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get dashboard stats", description = "Get overall dashboard statistics")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        DashboardStatsResponse stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/branch-stats/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get branch stats", description = "Get statistics for a specific branch")
    public ResponseEntity<ApiResponse<BranchStatsResponse>> getBranchStats(@PathVariable Long branchId) {
        BranchStatsResponse stats = dashboardService.getBranchStats(branchId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}