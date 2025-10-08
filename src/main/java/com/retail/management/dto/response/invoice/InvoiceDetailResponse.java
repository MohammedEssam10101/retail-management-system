package com.retail.management.dto.response.invoice;

import com.retail.management.dto.response.payment.PaymentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetailResponse {
    private InvoiceResponse invoice;
    private List<PaymentResponse> payments;
    private List<DiscountResponse> discounts;
}