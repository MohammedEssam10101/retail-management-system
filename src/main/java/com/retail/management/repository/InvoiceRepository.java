package com.retail.management.repository;

import com.retail.management.entity.Invoice;
import com.retail.management.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>,
        JpaSpecificationExecutor<Invoice> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findByIdempotencyKey(String idempotencyKey);

    Boolean existsByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByBranchId(Long branchId);

    List<Invoice> findByCustomerId(Long customerId);

    List<Invoice> findByStatus(InvoiceStatus status);

    Page<Invoice> findByBranchId(Long branchId, Pageable pageable);

    Page<Invoice> findByCustomerId(Long customerId, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.branch.id = :branchId AND i.invoiceDate = :date")
    List<Invoice> findByBranchAndDate(@Param("branchId") Long branchId, @Param("date") LocalDate date);

    @Query("SELECT i FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate")
    List<Invoice> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT i FROM Invoice i WHERE i.branch.id = :branchId AND i.invoiceDate BETWEEN :startDate AND :endDate")
    List<Invoice> findByBranchAndDateRange(@Param("branchId") Long branchId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.branch.id = :branchId AND i.invoiceDate = :date AND i.status = 'PAID'")
    BigDecimal getTotalSalesByBranchAndDate(@Param("branchId") Long branchId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.branch.id = :branchId AND i.invoiceDate = :date")
    Long getInvoiceCountByBranchAndDate(@Param("branchId") Long branchId, @Param("date") LocalDate date);

    @Query("SELECT i FROM Invoice i WHERE i.outstandingBalance > 0")
    List<Invoice> findInvoicesWithOutstandingBalance();

    @Query("SELECT i FROM Invoice i WHERE i.cashier.id = :cashierId AND i.invoiceDate = :date")
    List<Invoice> findByCashierAndDate(@Param("cashierId") Long cashierId, @Param("date") LocalDate date);
}
