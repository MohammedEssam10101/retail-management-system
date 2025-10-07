package com.retail.management.repository;

import com.retail.management.entity.ReturnItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReturnItemRepository extends JpaRepository<ReturnItem, Long> {

    List<ReturnItem> findByReturnRecordId(Long returnId);

    List<ReturnItem> findByProductId(Long productId);

    @Query("SELECT ri FROM ReturnItem ri WHERE ri.returnRecord.branch.id = :branchId AND ri.returnRecord.returnDate = :date")
    List<ReturnItem> findByBranchAndDate(@Param("branchId") Long branchId, @Param("date") LocalDate date);

    @Query("SELECT ri.product.id, ri.product.name, SUM(ri.quantity) as totalReturned " +
            "FROM ReturnItem ri " +
            "WHERE ri.returnRecord.returnDate BETWEEN :startDate AND :endDate " +
            "GROUP BY ri.product.id, ri.product.name " +
            "ORDER BY totalReturned DESC")
    List<Object[]> findMostReturnedProducts(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
}