package com.retail.management.controller;

import com.retail.management.dto.response.ApiResponse;
import com.retail.management.dto.response.role.RoleResponse;
import com.retail.management.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "APIs for managing user roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all roles", description = "Retrieve all available roles")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success(roles));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get role by ID", description = "Retrieve role details by ID")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable Long id) {
        RoleResponse role = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.success(role));
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get role by name", description = "Retrieve role details by name")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleByName(@PathVariable String name) {
        RoleResponse role = roleService.getRoleByName(name);
        return ResponseEntity.ok(ApiResponse.success(role));
    }
}