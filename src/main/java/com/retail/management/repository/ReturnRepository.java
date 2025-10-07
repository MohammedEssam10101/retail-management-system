package com.retail.management.repository;

import com.retail.management.entity.Return;
import com.retail.management.enums.ReturnStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Long> {

    Optional<Return> findByReturnNumber(String returnNumber);

    Optional<Return> findByInvoiceId(Long invoiceId);

    List<Return> findByBranchId(Long branchId);

    List<Return> findByStatus(ReturnStatus status);

    Page<Return> findByBranchId(Long branchId, Pageable pageable);

    @Query("SELECT r FROM Return r WHERE r.returnDate BETWEEN :startDate AND :endDate")
    List<Return> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(r.totalReturnAmount) FROM Return r WHERE r.status = 'COMPLETED' AND r.returnDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalReturnsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(r) FROM Return r WHERE r.branch.id = :branchId AND r.returnDate = :date")
    Long getReturnCountByBranchAndDate(@Param("branchId") Long branchId, @Param("date") LocalDate date);
}
