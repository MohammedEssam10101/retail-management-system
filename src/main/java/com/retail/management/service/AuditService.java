package com.retail.management.service;

import com.retail.management.dto.response.audit.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditService {
    void logAction(String entityType, Long entityId, String action, String oldValues, String newValues);
    List<AuditLogResponse> getAuditTrail(String entityType, Long entityId);
    Page<AuditLogResponse> getUserActions(String username, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}