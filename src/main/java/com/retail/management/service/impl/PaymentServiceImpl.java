package com.retail.management.service.impl;

import com.retail.management.dto.request.payment.ProcessPaymentRequest;
import com.retail.management.dto.response.payment.PaymentResponse;
import com.retail.management.entity.Invoice;
import com.retail.management.entity.Payment;
import com.retail.management.enums.InvoiceStatus;
import com.retail.management.enums.PaymentStatus;
import com.retail.management.exception.ResourceNotFoundException;
import com.retail.management.mapper.PaymentMapper;
import com.retail.management.repository.InvoiceRepository;
import com.retail.management.repository.PaymentRepository;
import com.retail.management.security.SecurityUtils;
import com.retail.management.service.PaymentService;
import com.retail.management.validator.PaymentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentValidator paymentValidator;

    @Override
    @Transactional
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        log.info("Processing payment for invoice: {}", request.getInvoiceId());

        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        // Validate payment
        paymentValidator.validatePayment(invoice, request.getAmount());

        String username = SecurityUtils.getCurrentUsername().orElse("SYSTEM");

        // Create payment
        Payment payment = Payment.builder()
                .invoice(invoice)
                .paymentMethod(request.getPaymentMethod())
                .amount(request.getAmount())
                .referenceNumber(request.getReferenceNumber())
                .status(PaymentStatus.COMPLETED)
                .notes(request.getNotes())
                .paymentDate(LocalDateTime.now())
                .createdBy(username)
                .build();

        payment = paymentRepository.save(payment);

        // Update invoice
        BigDecimal newPaidAmount = invoice.getPaidAmount().add(request.getAmount());
        BigDecimal newOutstanding = invoice.getTotalAmount().subtract(newPaidAmount);

        invoice.setPaidAmount(newPaidAmount);
        invoice.setOutstandingBalance(newOutstanding);

        // Update invoice status
        if (newOutstanding.compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        invoiceRepository.save(invoice);

        log.info("Payment processed successfully for invoice: {}", invoice.getInvoiceNumber());
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByInvoice(Long invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId)
                .stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPaymentsByBranch(Long branchId, Pageable pageable) {
        return paymentRepository.findByInvoiceBranchId(branchId, pageable)
                .map(paymentMapper::toResponse);
    }
}