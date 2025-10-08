package com.retail.management.dto.response.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private Long id;
    private String entityType;
    private Long entityId;
    private String action;
    private String performedBy;
    private String oldValues;
    private String newValues;
    private String ipAddress;
    private String correlationId;
    private LocalDateTime createdAt;
}