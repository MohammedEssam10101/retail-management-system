package com.retail.management.service.impl;

import com.retail.management.dto.request.promo.CreatePromoCodeRequest;
import com.retail.management.dto.request.promo.UpdatePromoCodeRequest;
import com.retail.management.dto.response.promo.PromoCodeResponse;
import com.retail.management.entity.PromoCode;
import com.retail.management.enums.PromoCodeStatus;
import com.retail.management.exception.DuplicateResourceException;
import com.retail.management.exception.ResourceNotFoundException;
import com.retail.management.mapper.PromoCodeMapper;
import com.retail.management.repository.PromoCodeRepository;
import com.retail.management.service.PromoCodeService;
import com.retail.management.validator.PromoCodeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromoCodeServiceImpl implements PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeMapper promoCodeMapper;
    private final PromoCodeValidator promoCodeValidator;

    @Override
    @Transactional
    public PromoCodeResponse createPromoCode(CreatePromoCodeRequest request) {
        log.info("Creating promo code: {}", request.getCode());

        if (promoCodeRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("PromoCode", "code", request.getCode());
        }

        PromoCode promoCode = promoCodeMapper.toEntity(request);
        promoCode = promoCodeRepository.save(promoCode);

        log.info("Promo code created successfully: {}", promoCode.getCode());
        return promoCodeMapper.toResponse(promoCode);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "promo-codes", key = "#id")
    public PromoCodeResponse getPromoCodeById(Long id) {
        PromoCode promoCode = promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PromoCode", "id", id));
        return promoCodeMapper.toResponse(promoCode);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PromoCodeResponse> getAllPromoCodes(Pageable pageable) {
        return promoCodeRepository.findAllNotDeleted(pageable)
                .map(promoCodeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromoCodeResponse> getActivePromoCodes() {
        LocalDate today = LocalDate.now();
        return promoCodeRepository.findValidPromoCodesForDate(today)
                .stream()
                .map(promoCodeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "promo-codes", key = "#id")
    public PromoCodeResponse updatePromoCode(Long id, UpdatePromoCodeRequest request) {
        log.info("Updating promo code: {}", id);

        PromoCode promoCode = promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PromoCode", "id", id));

        promoCodeMapper.updateEntityFromRequest(request, promoCode);
        promoCode = promoCodeRepository.save(promoCode);

        log.info("Promo code updated successfully: {}", promoCode.getCode());
        return promoCodeMapper.toResponse(promoCode);
    }

    @Override
    @Transactional
    @CacheEvict(value = "promo-codes", key = "#id")
    public void deletePromoCode(Long id) {
        log.info("Soft deleting promo code: {}", id);

        PromoCode promoCode = promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PromoCode", "id", id));

        promoCode.setDeletedAt(LocalDateTime.now());
        promoCode.setActive(false);
        promoCodeRepository.save(promoCode);

        log.info("Promo code soft deleted: {}", promoCode.getCode());
    }

    @Override
    @Transactional
    public PromoCode validateAndApplyPromoCode(String code, BigDecimal invoiceAmount) {
        return promoCodeValidator.validatePromoCode(code);
    }
}