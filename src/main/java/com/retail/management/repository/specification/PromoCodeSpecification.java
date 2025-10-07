package com.retail.management.repository.specification;

import com.retail.management.entity.PromoCode;
import com.retail.management.enums.DiscountType;
import com.retail.management.enums.PromoCodeStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PromoCodeSpecification {

    public static Specification<PromoCode> filterPromoCodes(
            String searchTerm,
            DiscountType discountType,
            PromoCodeStatus status,
            Boolean active,
            LocalDate validDate) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter out soft-deleted promo codes
            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

            // Search term (code, description)
            if (searchTerm != null && !searchTerm.isEmpty()) {
                String likePattern = "%" + searchTerm.toLowerCase() + "%";
                Predicate codePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("code")), likePattern);
                Predicate descPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")), likePattern);
                predicates.add(criteriaBuilder.or(codePredicate, descPredicate));
            }

            // Filter by discount type
            if (discountType != null) {
                predicates.add(criteriaBuilder.equal(root.get("discountType"), discountType));
            }

            // Filter by status
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // Filter by active flag
            if (active != null) {
                predicates.add(criteriaBuilder.equal(root.get("active"), active));
            }

            // Filter by validity date
            if (validDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("validFrom"), validDate));
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("validUntil"), validDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<PromoCode> isActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("active"), true),
                        criteriaBuilder.equal(root.get("status"), PromoCodeStatus.ACTIVE),
                        criteriaBuilder.isNull(root.get("deletedAt"))
                );
    }

    public static Specification<PromoCode> isValidForDate(LocalDate date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.lessThanOrEqualTo(root.get("validFrom"), date),
                        criteriaBuilder.greaterThanOrEqualTo(root.get("validUntil"), date),
                        criteriaBuilder.equal(root.get("active"), true),
                        criteriaBuilder.equal(root.get("status"), PromoCodeStatus.ACTIVE)
                );
    }

    public static Specification<PromoCode> hasUsageAvailable() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.isNull(root.get("usageLimit")),
                        criteriaBuilder.lessThan(root.get("timesUsed"), root.get("usageLimit"))
                );
    }

    public static Specification<PromoCode> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("deletedAt"));
    }
}