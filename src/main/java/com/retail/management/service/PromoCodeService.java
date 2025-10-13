package com.retail.management.service;

import com.retail.management.dto.request.promo.CreatePromoCodeRequest;
import com.retail.management.dto.request.promo.UpdatePromoCodeRequest;
import com.retail.management.dto.response.promo.PromoCodeResponse;
import com.retail.management.entity.PromoCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PromoCodeService {
    PromoCodeResponse createPromoCode(CreatePromoCodeRequest request);
    PromoCodeResponse getPromoCodeById(Long id);
    Page<PromoCodeResponse> getAllPromoCodes(Pageable pageable);
    List<PromoCodeResponse> getActivePromoCodes();
    PromoCodeResponse updatePromoCode(Long id, UpdatePromoCodeRequest request);
    void deletePromoCode(Long id);
    PromoCode validateAndApplyPromoCode(String code, java.math.BigDecimal invoiceAmount);
}