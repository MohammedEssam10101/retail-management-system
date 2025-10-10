package com.retail.management.mapper;

import com.retail.management.dto.response.audit.AuditLogResponse;
import com.retail.management.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuditLogMapper {

    AuditLogResponse toResponse(AuditLog auditLog);
}
