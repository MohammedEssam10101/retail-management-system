package com.retail.management.mapper;

import com.retail.management.dto.request.product.CreateProductRequest;
import com.retail.management.dto.request.product.UpdateProductRequest;
import com.retail.management.dto.response.product.ProductResponse;
import com.retail.management.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    Product toEntity(CreateProductRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sku", ignore = true)
    void updateEntityFromRequest(UpdateProductRequest request, @MappingTarget Product product);
}
