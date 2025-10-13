package com.retail.management.service;

import com.retail.management.dto.request.invoice.CreateInvoiceRequest;
import com.retail.management.dto.request.search.InvoiceFilterRequest;
import com.retail.management.dto.response.invoice.InvoiceDetailResponse;
import com.retail.management.dto.response.invoice.InvoiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvoiceService {
    InvoiceResponse createInvoice(CreateInvoiceRequest request);
    InvoiceDetailResponse getInvoiceById(Long id);
    Page<InvoiceResponse> getAllInvoices(Pageable pageable);
    Page<InvoiceResponse> filterInvoices(InvoiceFilterRequest filter, Pageable pageable);
    InvoiceResponse cancelInvoice(Long id);
}