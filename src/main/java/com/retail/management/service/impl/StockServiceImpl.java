package com.retail.management.service.impl;

import com.retail.management.dto.request.product.StockAdjustmentRequest;
import com.retail.management.dto.request.stock.StockTransferRequest;
import com.retail.management.dto.response.stock.StockLevelResponse;
import com.retail.management.entity.BranchStock;
import com.retail.management.entity.StockAdjustment;
import com.retail.management.exception.BusinessException;
import com.retail.management.exception.InsufficientStockException;
import com.retail.management.exception.ResourceNotFoundException;
import com.retail.management.repository.BranchRepository;
import com.retail.management.repository.BranchStockRepository;
import com.retail.management.repository.ProductRepository;
import com.retail.management.repository.StockAdjustmentRepository;
import com.retail.management.security.SecurityUtils;
import com.retail.management.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements StockService {

    private final BranchStockRepository branchStockRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;

    @Override
    @Transactional
    public void adjustStock(StockAdjustmentRequest request) {
        log.info("Adjusting stock for product: {} at branch: {}", request.getProductId(), request.getBranchId());

        var branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        var product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        BranchStock stock = branchStockRepository
                .findByBranchIdAndProductId(request.getBranchId(), request.getProductId())
                .orElseGet(() -> {
                    BranchStock newStock = BranchStock.builder()
                            .branch(branch)
                            .product(product)
                            .quantity(0)
                            .reservedQuantity(0)
                            .build();
                    return branchStockRepository.save(newStock);
                });

        int quantityBefore = stock.getQuantity();
        int quantityChange = request.getAdjustmentType().isIncreasesStock()
                ? request.getQuantity()
                : -request.getQuantity();
        int quantityAfter = quantityBefore + quantityChange;

        if (quantityAfter < 0) {
            throw new InsufficientStockException(
                    product.getName(),
                    quantityBefore,
                    Math.abs(quantityChange)
            );
        }

        stock.setQuantity(quantityAfter);
        if (request.getAdjustmentType().name().contains("RESTOCK")) {
            stock.setLastRestockedAt(java.time.LocalDateTime.now());
        }
        branchStockRepository.save(stock);

        // Create adjustment record
        Long userId = SecurityUtils.getCurrentUsername()
                .flatMap(username -> com.retail.management.repository.UserRepository.class.cast(null).findByUsername(username))
                .map(com.retail.management.entity.User::getId)
                .orElse(null);

        StockAdjustment adjustment = StockAdjustment.builder()
                .branch(branch)
                .product(product)
                .adjustmentType(request.getAdjustmentType())
                .quantityChange(request.getQuantity())
                .quantityBefore(quantityBefore)
                .quantityAfter(quantityAfter)
                .reason(request.getReason())
                .notes(request.getNotes())
                .adjustedBy(userId)
                .build();

        stockAdjustmentRepository.save(adjustment);
        log.info("Stock adjusted successfully");
    }

    @Override
    @Transactional
    public void transferStock(StockTransferRequest request) {
        log.info("Transferring stock from branch {} to branch {}",
                request.getFromBranchId(), request.getToBranchId());

        if (request.getFromBranchId().equals(request.getToBranchId())) {
            throw new BusinessException("Cannot transfer stock to the same branch");
        }

        // Reduce from source branch
        BranchStock fromStock = branchStockRepository
                .findByBranchIdAndProductId(request.getFromBranchId(), request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found in source branch"));

        if (fromStock.getQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(
                    fromStock.getProduct().getName(),
                    fromStock.getQuantity(),
                    request.getQuantity()
            );
        }

        fromStock.setQuantity(fromStock.getQuantity() - request.getQuantity());
        branchStockRepository.save(fromStock);

        // Add to destination branch
        BranchStock toStock = branchStockRepository
                .findByBranchIdAndProductId(request.getToBranchId(), request.getProductId())
                .orElseGet(() -> {
                    var toBranch = branchRepository.findById(request.getToBranchId())
                            .orElseThrow(() -> new ResourceNotFoundException("Destination branch not found"));
                    var product = productRepository.findById(request.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

                    return BranchStock.builder()
                            .branch(toBranch)
                            .product(product)
                            .quantity(0)
                            .reservedQuantity(0)
                            .build();
                });

        toStock.setQuantity(toStock.getQuantity() + request.getQuantity());
        branchStockRepository.save(toStock);

        log.info("Stock transferred successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public StockLevelResponse getStockLevel(Long branchId, Long productId) {
        BranchStock stock = branchStockRepository
                .findByBranchIdAndProductId(branchId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

        return StockLevelResponse.builder()
                .branchId(stock.getBranch().getId())
                .branchName(stock.getBranch().getName())
                .productId(stock.getProduct().getId())
                .productName(stock.getProduct().getName())
                .sku(stock.getProduct().getSku())
                .quantity(stock.getQuantity())
                .reservedQuantity(stock.getReservedQuantity())
                .availableQuantity(stock.getQuantity() - stock.getReservedQuantity())
                .lowStockThreshold(stock.getProduct().getLowStockThreshold())
                .isLowStock(stock.getQuantity() < stock.getProduct().getLowStockThreshold())
                .lastRestockedAt(stock.getLastRestockedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockLevelResponse> getLowStockItems(Long branchId) {
        return branchStockRepository.findLowStockByBranch(branchId)
                .stream()
                .map(stock -> StockLevelResponse.builder()
                        .branchId(stock.getBranch().getId())
                        .branchName(stock.getBranch().getName())
                        .productId(stock.getProduct().getId())
                        .productName(stock.getProduct().getName())
                        .sku(stock.getProduct().getSku())
                        .quantity(stock.getQuantity())
                        .reservedQuantity(stock.getReservedQuantity())
                        .availableQuantity(stock.getQuantity() - stock.getReservedQuantity())
                        .lowStockThreshold(stock.getProduct().getLowStockThreshold())
                        .isLowStock(true)
                        .lastRestockedAt(stock.getLastRestockedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalStockAcrossBranches(Long productId) {
        return branchStockRepository.getTotalQuantityAcrossAllBranches(productId);
    }
}

