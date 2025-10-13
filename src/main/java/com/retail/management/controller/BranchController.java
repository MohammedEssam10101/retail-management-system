package com.retail.management.controller;

import com.retail.management.dto.request.branch.CreateBranchRequest;
import com.retail.management.dto.request.branch.UpdateBranchRequest;
import com.retail.management.dto.response.ApiResponse;
import com.retail.management.dto.response.branch.BranchResponse;
import com.retail.management.service.BranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
@Tag(name = "Branch Management", description = "APIs for managing branches")
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create branch", description = "Create a new branch")
    public ResponseEntity<ApiResponse<BranchResponse>> createBranch(
            @Valid @RequestBody CreateBranchRequest request) {
        BranchResponse branch = branchService.createBranch(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(branch, "Branch created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get branch by ID", description = "Retrieve branch details by ID")
    public ResponseEntity<ApiResponse<BranchResponse>> getBranchById(@PathVariable Long id) {
        BranchResponse branch = branchService.getBranchById(id);
        return ResponseEntity.ok(ApiResponse.success(branch));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all branches", description = "Retrieve all branches with pagination")
    public ResponseEntity<ApiResponse<Page<BranchResponse>>> getAllBranches(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<BranchResponse> branches = branchService.getAllBranches(pageable);
        return ResponseEntity.ok(ApiResponse.success(branches));
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get active branches", description = "Retrieve all active branches")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getActiveBranches() {
        List<BranchResponse> branches = branchService.getActiveBranches();
        return ResponseEntity.ok(ApiResponse.success(branches));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update branch", description = "Update branch details")
    public ResponseEntity<ApiResponse<BranchResponse>> updateBranch(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBranchRequest request) {
        BranchResponse branch = branchService.updateBranch(id, request);
        return ResponseEntity.ok(ApiResponse.success(branch, "Branch updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete branch", description = "Soft delete branch")
    public ResponseEntity<ApiResponse<Void>> deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Branch deleted successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate branch", description = "Deactivate a branch")
    public ResponseEntity<ApiResponse<Void>> deactivateBranch(@PathVariable Long id) {
        branchService.deactivateBranch(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Branch deactivated successfully"));
    }
}