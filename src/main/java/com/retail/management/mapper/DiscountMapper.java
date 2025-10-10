package com.retail.management.mapper;

import com.retail.management.dto.response.invoice.DiscountResponse;
import com.retail.management.entity.Discount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DiscountMapper {

    @Mapping(target = "promoCode", source = "promoCode.code")
    DiscountResponse toResponse(Discount discount);
}