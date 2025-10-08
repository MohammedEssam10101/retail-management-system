package com.retail.management.dto.response.customer;

import com.retail.management.dto.response.invoice.InvoiceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPurchaseHistoryResponse {
    private CustomerResponse customer;
    private Page<InvoiceResponse> invoices;
    private BigDecimal totalSpent;
    private Long totalInvoices;
}