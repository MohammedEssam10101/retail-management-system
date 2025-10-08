package com.retail.management.dto.request.promo;

import com.retail.management.enums.DiscountType;
import com.retail.management.enums.PromoCodeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePromoCodeRequest {

    @Size(max = 1000)
    private String description;

    private DiscountType discountType;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal discountValue;

    @DecimalMin(value = "0.0")
    private BigDecimal maxDiscountAmount;

    @DecimalMin(value = "0.0")
    private BigDecimal minPurchaseAmount;

    @Min(value = 1)
    private Integer usageLimit;

    private LocalDate validFrom;

    private LocalDate validUntil;

    private PromoCodeStatus status;

    private Boolean active;
}
