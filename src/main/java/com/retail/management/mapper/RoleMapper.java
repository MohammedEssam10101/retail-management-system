package com.retail.management.mapper;

import com.retail.management.dto.response.role.RoleResponse;
import com.retail.management.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    RoleResponse toResponse(Role role);
}