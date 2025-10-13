package com.retail.management.controller;

import com.retail.management.dto.request.returnDto.CreateReturnRequest;
import com.retail.management.dto.response.ApiResponse;
import com.retail.management.dto.response.returnDto.ReturnResponse;
import com.retail.management.service.ReturnService;
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

@RestController
@RequestMapping("/api/v1/returns")
@RequiredArgsConstructor
@Tag(name = "Return Management", description = "APIs for managing product returns")
public class ReturnController {

    private final ReturnService returnService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    @Operation(summary = "Create return", description = "Create a product return")
    public ResponseEntity<ApiResponse<ReturnResponse>> createReturn(
            @Valid @RequestBody CreateReturnRequest request) {
        ReturnResponse returnResponse = returnService.createReturn(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(returnResponse, "Return created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get return by ID", description = "Retrieve return details by ID")
    public ResponseEntity<ApiResponse<ReturnResponse>> getReturnById(@PathVariable Long id) {
        ReturnResponse returnResponse = returnService.getReturnById(id);
        return ResponseEntity.ok(ApiResponse.success(returnResponse));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all returns", description = "Retrieve all returns with pagination")
    public ResponseEntity<ApiResponse<Page<ReturnResponse>>> getAllReturns(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ReturnResponse> returns = returnService.getAllReturns(pageable);
        return ResponseEntity.ok(ApiResponse.success(returns));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Approve return", description = "Approve a product return")
    public ResponseEntity<ApiResponse<ReturnResponse>> approveReturn(@PathVariable Long id) {
        ReturnResponse returnResponse = returnService.approveReturn(id);
        return ResponseEntity.ok(ApiResponse.success(returnResponse, "Return approved successfully"));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Reject return", description = "Reject a product return")
    public ResponseEntity<ApiResponse<ReturnResponse>> rejectReturn(
            @PathVariable Long id,
            @RequestParam String reason) {
        ReturnResponse returnResponse = returnService.rejectReturn(id, reason);
        return ResponseEntity.ok(ApiResponse.success(returnResponse, "Return rejected"));
    }
}