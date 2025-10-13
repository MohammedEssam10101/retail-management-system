package com.retail.management.controller;

import com.retail.management.dto.request.invoice.CreateInvoiceRequest;
import com.retail.management.dto.request.search.InvoiceFilterRequest;
import com.retail.management.dto.response.ApiResponse;
import com.retail.management.dto.response.invoice.InvoiceDetailResponse;
import com.retail.management.dto.response.invoice.InvoiceResponse;
import com.retail.management.service.InvoiceService;
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
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoice Management", description = "APIs for managing invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    @Operation(summary = "Create invoice", description = "Create a new invoice")
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(
            @Valid @RequestBody CreateInvoiceRequest request) {
        InvoiceResponse invoice = invoiceService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(invoice, "Invoice created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get invoice by ID", description = "Retrieve invoice details by ID")
    public ResponseEntity<ApiResponse<InvoiceDetailResponse>> getInvoiceById(@PathVariable Long id) {
        InvoiceDetailResponse invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponse.success(invoice));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all invoices", description = "Retrieve all invoices with pagination")
    public ResponseEntity<ApiResponse<Page<InvoiceResponse>>> getAllInvoices(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<InvoiceResponse> invoices = invoiceService.getAllInvoices(pageable);
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    @GetMapping("/filter")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Filter invoices", description = "Filter invoices with criteria")
    public ResponseEntity<ApiResponse<Page<InvoiceResponse>>> filterInvoices(
            InvoiceFilterRequest filter,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<InvoiceResponse> invoices = invoiceService.filterInvoices(filter, pageable);
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Cancel invoice", description = "Cancel an invoice")
    public ResponseEntity<ApiResponse<InvoiceResponse>> cancelInvoice(@PathVariable Long id) {
        InvoiceResponse invoice = invoiceService.cancelInvoice(id);
        return ResponseEntity.ok(ApiResponse.success(invoice, "Invoice cancelled successfully"));
    }
}