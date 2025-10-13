package com.retail.management.controller;

import com.retail.management.dto.request.product.StockAdjustmentRequest;
import com.retail.management.dto.request.stock.StockTransferRequest;
import com.retail.management.dto.response.ApiResponse;
import com.retail.management.dto.response.stock.StockLevelResponse;
import com.retail.management.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
@Tag(name = "Stock Management", description = "APIs for managing inventory stock")
public class StockController {

    private final StockService stockService;

    @PostMapping("/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Adjust stock", description = "Adjust stock quantity for a product")
    public ResponseEntity<ApiResponse<Void>> adjustStock(@Valid @RequestBody StockAdjustmentRequest request) {
        stockService.adjustStock(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Stock adjusted successfully"));
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Transfer stock", description = "Transfer stock between branches")
    public ResponseEntity<ApiResponse<Void>> transferStock(@Valid @RequestBody StockTransferRequest request) {
        stockService.transferStock(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Stock transferred successfully"));
    }

    @GetMapping("/level/{branchId}/{productId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get stock level", description = "Get stock level for a product in a branch")
    public ResponseEntity<ApiResponse<StockLevelResponse>> getStockLevel(
            @PathVariable Long branchId,
            @PathVariable Long productId) {
        StockLevelResponse stock = stockService.getStockLevel(branchId, productId);
        return ResponseEntity.ok(ApiResponse.success(stock));
    }

    @GetMapping("/low-stock/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get low stock items", description = "Get products with low stock in a branch")
    public ResponseEntity<ApiResponse<List<StockLevelResponse>>> getLowStockItems(@PathVariable Long branchId) {
        List<StockLevelResponse> lowStockItems = stockService.getLowStockItems(branchId);
        return ResponseEntity.ok(ApiResponse.success(lowStockItems));
    }

    @GetMapping("/total/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get total stock", description = "Get total stock across all branches for a product")
    public ResponseEntity<ApiResponse<Integer>> getTotalStockAcrossBranches(@PathVariable Long productId) {
        Integer totalStock = stockService.getTotalStockAcrossBranches(productId);
        return ResponseEntity.ok(ApiResponse.success(totalStock));
    }
}
