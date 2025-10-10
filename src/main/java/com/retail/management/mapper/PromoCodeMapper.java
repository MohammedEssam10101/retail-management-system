package com.retail.management.mapper;

import com.retail.management.dto.request.promo.CreatePromoCodeRequest;
import com.retail.management.dto.request.promo.UpdatePromoCodeRequest;
import com.retail.management.dto.response.promo.PromoCodeResponse;
import com.retail.management.entity.PromoCode;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PromoCodeMapper {

    PromoCodeResponse toResponse(PromoCode promoCode);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timesUsed", constant = "0")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "active", constant = "true")
    PromoCode toEntity(CreatePromoCodeRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "timesUsed", ignore = true)
    void updateEntityFromRequest(UpdatePromoCodeRequest request, @MappingTarget PromoCode promoCode);
}