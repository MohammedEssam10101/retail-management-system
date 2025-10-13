package com.retail.management.service;

import com.retail.management.dto.response.role.RoleResponse;

import java.util.List;

public interface RoleService {
    List<RoleResponse> getAllRoles();
    RoleResponse getRoleById(Long id);
    RoleResponse getRoleByName(String name);
}