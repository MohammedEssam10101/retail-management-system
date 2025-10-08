package com.retail.management.dto.request.invoice;

import com.retail.management.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyDiscountRequest {

    @NotNull(message = "Discount type is required")
    private DiscountType discountType;

    private BigDecimal discountValue;

    private String promoCode;

    private String description;
}