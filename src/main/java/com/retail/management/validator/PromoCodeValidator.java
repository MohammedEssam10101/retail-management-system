package com.retail.management.validator;

import com.retail.management.entity.PromoCode;
import com.retail.management.enums.PromoCodeStatus;
import com.retail.management.exception.InvalidPromoCodeException;
import com.retail.management.repository.PromoCodeRepository;
import com.retail.management.validator.annotation.ValidPromoCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PromoCodeValidator implements ConstraintValidator<ValidPromoCode, String> {

    private final PromoCodeRepository promoCodeRepository;

    /**
     * Validate promo code existence and status
     */
    @Override
    public boolean isValid(String code, ConstraintValidatorContext context) {
        Optional<PromoCode> promoCodeOpt = promoCodeRepository.findByCode(code);

        if (promoCodeOpt.isEmpty()) {
            throw new InvalidPromoCodeException("Promo code not found: " + code);
        }

        PromoCode promoCode = promoCodeOpt.get();

        // Check if active
        if (!promoCode.getActive()) {
            throw new InvalidPromoCodeException("Promo code is inactive");
        }

        // Check status
        if (promoCode.getStatus() != PromoCodeStatus.ACTIVE) {
            throw new InvalidPromoCodeException("Promo code is not active");
        }

        // Check validity dates
        LocalDate today = LocalDate.now();
        if (today.isBefore(promoCode.getValidFrom())) {
            throw new InvalidPromoCodeException("Promo code is not yet valid");
        }

        if (today.isAfter(promoCode.getValidUntil())) {
            throw new InvalidPromoCodeException("Promo code has expired");
        }

        // Check usage limit
        if (promoCode.getUsageLimit() != null &&
                promoCode.getTimesUsed() >= promoCode.getUsageLimit()) {
            throw new InvalidPromoCodeException("Promo code usage limit reached");
        }

        return true;
    }
}