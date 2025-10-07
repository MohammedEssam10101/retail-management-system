package com.retail.management.repository;

import com.retail.management.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    List<InvoiceItem> findByInvoiceId(Long invoiceId);

    List<InvoiceItem> findByProductId(Long productId);

    @Query("SELECT ii FROM InvoiceItem ii WHERE ii.invoice.branch.id = :branchId AND ii.invoice.invoiceDate = :date")
    List<InvoiceItem> findByBranchAndDate(@Param("branchId") Long branchId, @Param("date") LocalDate date);

    @Query("SELECT ii.product.id, ii.product.name, SUM(ii.quantity) as totalQuantity FROM InvoiceItem ii " +
            "WHERE ii.invoice.invoiceDate BETWEEN :startDate AND :endDate " +
            "GROUP BY ii.product.id, ii.product.name " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> findTopSellingProducts(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(ii.quantity) FROM InvoiceItem ii WHERE ii.product.id = :productId")
    Integer getTotalQuantitySoldByProduct(@Param("productId") Long productId);
}
