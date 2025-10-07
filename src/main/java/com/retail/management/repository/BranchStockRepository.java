package com.retail.management.repository;

import com.retail.management.entity.BranchStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchStockRepository extends JpaRepository<BranchStock, Long> {

    Optional<BranchStock> findByBranchIdAndProductId(Long branchId, Long productId);

    List<BranchStock> findByBranchId(Long branchId);

    List<BranchStock> findByProductId(Long productId);

    @Query("SELECT bs FROM BranchStock bs WHERE bs.branchId = :branchId AND bs.quantity < bs.product.lowStockThreshold")
    List<BranchStock> findLowStockByBranch(@Param("branchId") Long branchId);

    @Query("SELECT bs FROM BranchStock bs WHERE bs.quantity = 0")
    List<BranchStock> findOutOfStockItems();

    @Query("SELECT SUM(bs.quantity) FROM BranchStock bs WHERE bs.product.id = :productId")
    Integer getTotalQuantityAcrossAllBranches(@Param("productId") Long productId);

    @Query("SELECT bs FROM BranchStock bs WHERE bs.branch.id = :branchId AND bs.quantity > 0")
    List<BranchStock> findAvailableStockByBranch(@Param("branchId") Long branchId);

    @Query("SELECT bs FROM BranchStock WHERE bs.branch.id = :branchId " +
            "ORDER BY bs.LastRestockedAt DESC")
    List<BranchStock> findRecentlyRestockedByBranch(@Param("branchId") Long branchId);
}
