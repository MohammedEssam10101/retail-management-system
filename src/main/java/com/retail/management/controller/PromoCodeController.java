package com.retail.management.controller;

import com.retail.management.dto.request.promo.CreatePromoCodeRequest;
import com.retail.management.dto.request.promo.UpdatePromoCodeRequest;
import com.retail.management.dto.response.ApiResponse;
import com.retail.management.dto.response.promo.PromoCodeResponse;
import com.retail.management.service.PromoCodeService;
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
@RequestMapping("/api/v1/promo-codes")
@RequiredArgsConstructor
@Tag(name = "Promo Code Management", description = "APIs for managing promotional codes")
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create promo code", description = "Create a new promotional code")
    public ResponseEntity<ApiResponse<PromoCodeResponse>> createPromoCode(
            @Valid @RequestBody CreatePromoCodeRequest request) {
        PromoCodeResponse promoCode = promoCodeService.createPromoCode(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(promoCode, "Promo code created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get promo code by ID", description = "Retrieve promo code details by ID")
    public ResponseEntity<ApiResponse<PromoCodeResponse>> getPromoCodeById(@PathVariable Long id) {
        PromoCodeResponse promoCode = promoCodeService.getPromoCodeById(id);
        return ResponseEntity.ok(ApiResponse.success(promoCode));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all promo codes", description = "Retrieve all promo codes with pagination")
    public ResponseEntity<ApiResponse<Page<PromoCodeResponse>>> getAllPromoCodes(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PromoCodeResponse> promoCodes = promoCodeService.getAllPromoCodes(pageable);
        return ResponseEntity.ok(ApiResponse.success(promoCodes));
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get active promo codes", description = "Retrieve all currently active promo codes")
    public ResponseEntity<ApiResponse<List<PromoCodeResponse>>> getActivePromoCodes() {
        List<PromoCodeResponse> promoCodes = promoCodeService.getActivePromoCodes();
        return ResponseEntity.ok(ApiResponse.success(promoCodes));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update promo code", description = "Update promo code details")
    public ResponseEntity<ApiResponse<PromoCodeResponse>> updatePromoCode(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePromoCodeRequest request) {
        PromoCodeResponse promoCode = promoCodeService.updatePromoCode(id, request);
        return ResponseEntity.ok(ApiResponse.success(promoCode, "Promo code updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete promo code", description = "Soft delete a promo code")
    public ResponseEntity<ApiResponse<Void>> deletePromoCode(@PathVariable Long id) {
        promoCodeService.deletePromoCode(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Promo code deleted successfully"));
    }
}