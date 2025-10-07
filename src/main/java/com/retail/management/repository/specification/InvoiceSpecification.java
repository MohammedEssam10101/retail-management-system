package com.retail.management.repository.specification;

import com.retail.management.entity.Invoice;
import com.retail.management.enums.InvoiceStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceSpecification {

    public static Specification<Invoice> filterInvoices(
            Long branchId,
            Long customerId,
            Long cashierId,
            InvoiceStatus status,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String customerType) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter out soft-deleted invoices
            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

            // Filter by branch
            if (branchId != null) {
                predicates.add(criteriaBuilder.equal(root.get("branch").get("id"), branchId));
            }

            // Filter by customer
            if (customerId != null) {
                predicates.add(criteriaBuilder.equal(root.get("customer").get("id"), customerId));
            }

            // Filter by cashier
            if (cashierId != null) {
                predicates.add(criteriaBuilder.equal(root.get("cashier").get("id"), cashierId));
            }

            // Filter by status
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // Filter by date range
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("invoiceDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("invoiceDate"), endDate));
            }

            // Filter by amount range
            if (minAmount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalAmount"), minAmount));
            }
            if (maxAmount != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("totalAmount"), maxAmount));
            }

            // Filter by customer type
            if (customerType != null && !customerType.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("customerType"), customerType));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Invoice> hasBranch(Long branchId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("branch").get("id"), branchId);
    }

    public static Specification<Invoice> hasStatus(InvoiceStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Invoice> dateInRange(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("invoiceDate"), startDate, endDate);
    }

    public static Specification<Invoice> hasOutstandingBalance() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("outstandingBalance"), BigDecimal.ZERO);
    }

    public static Specification<Invoice> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("deletedAt"));
    }

    public static Specification<Invoice> createdToday() {
        LocalDate today = LocalDate.now();
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("invoiceDate"), today);
    }
}