package com.retail.management.service;

import com.retail.management.dto.response.stats.BranchStatsResponse;
import com.retail.management.dto.response.stats.DashboardStatsResponse;

public interface DashboardService {
    DashboardStatsResponse getDashboardStats();
    BranchStatsResponse getBranchStats(Long branchId);
}