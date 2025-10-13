package com.retail.management.controller;

import com.retail.management.dto.request.payment.ProcessPaymentRequest;
import com.retail.management.dto.response.ApiResponse;
import com.retail.management.dto.response.payment.PaymentResponse;
import com.retail.management.service.PaymentService;
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
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "APIs for managing payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    @Operation(summary = "Process payment", description = "Process a payment for an invoice")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody ProcessPaymentRequest request) {
        PaymentResponse payment = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(payment, "Payment processed successfully"));
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get payments by invoice", description = "Retrieve all payments for an invoice")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByInvoice(
            @PathVariable Long invoiceId) {
        List<PaymentResponse> payments = paymentService.getPaymentsByInvoice(invoiceId);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get payments by branch", description = "Retrieve payments by branch")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getPaymentsByBranch(
            @PathVariable Long branchId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PaymentResponse> payments = paymentService.getPaymentsByBranch(branchId, pageable);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }
}