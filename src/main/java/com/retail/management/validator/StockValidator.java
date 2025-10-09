package com.retail.management.validator;

import com.retail.management.entity.BranchStock;
import com.retail.management.exception.InsufficientStockException;
import com.retail.management.repository.BranchStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockValidator {

    private final BranchStockRepository branchStockRepository;

    /**
     * Validate stock availability for a product in a branch
     */
    public void validateStockAvailability(Long branchId, Long productId, Integer requestedQuantity) {
        BranchStock stock = branchStockRepository
                .findByBranchIdAndProductId(branchId, productId)
                .orElseThrow(() -> new InsufficientStockException(
                        "Product not available in this branch"));

        int availableQuantity = stock.getQuantity() - stock.getReservedQuantity();

        if (availableQuantity < requestedQuantity) {
            throw new InsufficientStockException(
                    stock.getProduct().getName(),
                    availableQuantity,
                    requestedQuantity
            );
        }
    }

    /**
     * Check if product stock is low
     */
    public boolean isLowStock(BranchStock stock) {
        return stock.getQuantity() < stock.getProduct().getLowStockThreshold();
    }
}