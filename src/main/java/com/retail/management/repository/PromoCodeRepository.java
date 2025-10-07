package com.retail.management.repository;

import com.retail.management.entity.PromoCode;
import com.retail.management.enums.PromoCodeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {

    Optional<PromoCode> findByCode(String code);

    Boolean existsByCode(String code);

    List<PromoCode> findByStatus(PromoCodeStatus status);

    @Query("SELECT pc FROM PromoCode pc WHERE pc.active = true AND pc.status = 'ACTIVE' AND pc.deletedAt IS NULL")
    List<PromoCode> findAllActivePromoCodes();

    @Query("SELECT pc FROM PromoCode pc WHERE pc.validFrom <= :currentDate AND pc.validUntil >= :currentDate " +
            "AND pc.active = true AND pc.status = 'ACTIVE' AND pc.deletedAt IS NULL")
    List<PromoCode> findValidPromoCodesForDate(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT pc FROM PromoCode pc WHERE pc.validUntil < :currentDate AND pc.status = 'ACTIVE'")
    List<PromoCode> findExpiredPromoCodes(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT pc FROM PromoCode pc WHERE pc.deletedAt IS NULL")
    Page<PromoCode> findAllNotDeleted(Pageable pageable);
}

