package com.retail.management.repository;

import com.retail.management.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);

    List<AuditLog> findByPerformedBy(String performedBy);

    List<AuditLog> findByAction(String action);

    @Query("SELECT al FROM AuditLog al WHERE al.createdAt BETWEEN :startDate AND :endDate")
    List<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT al FROM AuditLog al WHERE al.entityType = :entityType AND al.entityId = :entityId ORDER BY al.createdAt DESC")
    List<AuditLog> findAuditTrail(@Param("entityType") String entityType, @Param("entityId") Long entityId);

    Page<AuditLog> findByPerformedBy(String performedBy, Pageable pageable);

    @Query("SELECT al FROM AuditLog al WHERE al.correlationId = :correlationId")
    List<AuditLog> findByCorrelationId(@Param("correlationId") String correlationId);
}
