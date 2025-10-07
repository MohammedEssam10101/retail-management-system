package com.retail.management.repository;

import com.retail.management.entity.Discount;
import com.retail.management.enums.DiscountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    List<Discount> findByInvoiceId(Long invoiceId);

    List<Discount> findByPromoCodeId(Long promoCodeId);

    List<Discount> findByDiscountType(DiscountType discountType);

    @Query("SELECT SUM(d.discountAmount) FROM Discount d WHERE d.appliedAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalDiscountsByDateRange(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT d.discountType, SUM(d.discountAmount), COUNT(d) FROM Discount d " +
            "WHERE d.appliedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY d.discountType")
    List<Object[]> getDiscountSummaryByType(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);
}
