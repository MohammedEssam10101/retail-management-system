package com.retail.management.service;

import com.retail.management.dto.request.product.StockAdjustmentRequest;
import com.retail.management.dto.request.stock.StockTransferRequest;
import com.retail.management.dto.response.stock.StockLevelResponse;
import com.retail.management.enums.StockAdjustmentType;

import java.util.List;

public interface StockService {
    void adjustStock(StockAdjustmentRequest request);
    void transferStock(StockTransferRequest request);
    StockLevelResponse getStockLevel(Long branchId, Long productId);
    List<StockLevelResponse> getLowStockItems(Long branchId);
    Integer getTotalStockAcrossBranches(Long productId);
}