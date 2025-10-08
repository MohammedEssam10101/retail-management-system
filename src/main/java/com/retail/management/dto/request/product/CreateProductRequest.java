package com.retail.management.dto.request.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "SKU is required")
    @Size(max = 50)
    private String sku;

    @NotBlank(message = "Product name is required")
    @Size(max = 200)
    private String name;

    @Size(max = 1000)
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Tax rate must be 0 or greater")
    @DecimalMax(value = "100.0", message = "Tax rate must not exceed 100")
    private BigDecimal taxRate;

    @Size(max = 100)
    private String category;

    @Size(max = 20)
    private String unit;

    @Min(value = 0, message = "Low stock threshold must be 0 or greater")
    private Integer lowStockThreshold;
}