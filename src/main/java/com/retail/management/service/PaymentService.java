package com.retail.management.service;

import com.retail.management.dto.request.payment.ProcessPaymentRequest;
import com.retail.management.dto.response.payment.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(ProcessPaymentRequest request);
    List<PaymentResponse> getPaymentsByInvoice(Long invoiceId);
    Page<PaymentResponse> getPaymentsByBranch(Long branchId, Pageable pageable);
}
