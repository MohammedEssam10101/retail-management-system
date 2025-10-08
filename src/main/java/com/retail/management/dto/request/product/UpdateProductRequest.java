package com.retail.management.dto.request.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    @Size(max = 200)
    private String name;

    @Size(max = 1000)
    private String description;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal taxRate;

    @Size(max = 100)
    private String category;

    @Size(max = 20)
    private String unit;

    @Min(value = 0)
    private Integer lowStockThreshold;

    private Boolean active;
}