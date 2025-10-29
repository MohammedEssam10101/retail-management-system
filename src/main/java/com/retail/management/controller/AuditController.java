package com.retail.management.controller;

import com.retail.management.dto.response.ApiResponse;
import com.retail.management.dto.response.audit.AuditLogResponse;
import com.retail.management.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Tag(name = "Audit Logs", description = "APIs for viewing audit logs and system activity")
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/trail/{entityType}/{entityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get audit trail", description = "Get audit trail for a specific entity")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditTrail(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        List<AuditLogResponse> trail = auditService.getAuditTrail(entityType, entityId);
        return ResponseEntity.ok(ApiResponse.success(trail));
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user actions", description = "Get all actions performed by a user")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getUserActions(
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 50) Pageable pageable) {

        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        Page<AuditLogResponse> actions = auditService.getUserActions(username, startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.success(actions));
    }
}