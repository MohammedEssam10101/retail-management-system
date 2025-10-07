package com.retail.management.repository;

import com.retail.management.entity.StockAdjustment;
import com.retail.management.enums.StockAdjustmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long> {

    List<StockAdjustment> findByBranchId(Long branchId);

    List<StockAdjustment> findByProductId(Long productId);

    List<StockAdjustment> findByAdjustmentType(StockAdjustmentType adjustmentType);

    @Query("SELECT sa FROM StockAdjustment sa Where sa.branch.id = :branchId And " +
            "sa.product.id = :productId ORDER BY sa.adjustmentDate DESC")
    List<StockAdjustment> findByBranchAndProduct(@Param("branchId") Long branchId,
                                                 @Param("productId") Long productId);

    Page<StockAdjustment> findByBranchId(Long branchId, Pageable pageable);
}
