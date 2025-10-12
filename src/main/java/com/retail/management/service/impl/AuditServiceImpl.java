package com.retail.management.service.impl;

import com.retail.management.dto.response.audit.AuditLogResponse;
import com.retail.management.entity.AuditLog;
import com.retail.management.mapper.AuditLogMapper;
import com.retail.management.repository.AuditLogRepository;
import com.retail.management.security.SecurityUtils;
import com.retail.management.service.AuditService;
import com.retail.management.util.CorrelationIdUtil;
import com.retail.management.util.IpAddressUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;
    private final HttpServletRequest request;

    @Override
    @Async("notificationExecutor")
    @Transactional
    public void logAction(String entityType, Long entityId, String action,
                          String oldValues, String newValues) {

        String username = SecurityUtils.getCurrentUsername().orElse("SYSTEM");
        String ipAddress = IpAddressUtil.getClientIpAddress(request);
        String correlationId = CorrelationIdUtil.get();

        AuditLog log = AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .performedBy(username)
                .oldValues(oldValues)
                .newValues(newValues)
                .ipAddress(ipAddress)
                .correlationId(correlationId)
                .build();

        auditLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditTrail(String entityType, Long entityId) {
        return auditLogRepository.findAuditTrail(entityType, entityId)
                .stream()
                .map(auditLogMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getUserActions(String username, LocalDateTime startDate,
                                                 LocalDateTime endDate, Pageable pageable) {
        return auditLogRepository.findByPerformedBy(username, pageable)
                .map(auditLogMapper::toResponse);
    }
}
