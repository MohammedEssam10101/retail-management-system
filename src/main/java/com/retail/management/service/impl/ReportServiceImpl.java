package com.retail.management.service.impl;

import com.retail.management.dto.request.search.ReportFilterRequest;
import com.retail.management.dto.response.report.*;
import com.retail.management.entity.Invoice;
import com.retail.management.enums.DiscountType;
import com.retail.management.enums.InvoiceStatus;
import com.retail.management.exception.ResourceNotFoundException;
import com.retail.management.repository.*;
import com.retail.management.service.ReportService;
import com.retail.management.util.CalculationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final BranchRepository branchRepository;
    private final PaymentRepository paymentRepository;
    private final DiscountRepository discountRepository;
    private final BranchStockRepository branchStockRepository;

    @Override
    @Transactional(readOnly = true)
    public DailySalesReport generateDailySalesReport(Long branchId, LocalDate date) {
        log.info("Generating daily sales report for branch: {} on date: {}", branchId, date);

        var branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        var invoices = invoiceRepository.findByBranchAndDate(branchId, date);

        BigDecimal totalSales = invoices.stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.PAID)
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTax = invoices.stream()
                .map(Invoice::getTaxAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDiscount = invoices.stream()
                .map(Invoice::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long invoiceCount = (long) invoices.size();

        // Get top products for the day
        List<ProductPerformanceDTO> topProducts = invoiceItemRepository
                .findTopSellingProducts(date, date)
                .stream()
                .limit(5)
                .map(row -> ProductPerformanceDTO.builder()
                        .productId((Long) row[0])
                        .productName((String) row[1])
                        .totalQuantitySold(((Number) row[2]).intValue())
                        .build())
                .collect(Collectors.toList());

        return DailySalesReport.builder()
                .branchId(branchId)
                .branchName(branch.getName())
                .date(date)
                .totalSales(totalSales)
                .totalTax(totalTax)
                .totalDiscount(totalDiscount)
                .invoiceCount(invoiceCount)
                .topProducts(topProducts)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SalesReportResponse generateSalesReport(ReportFilterRequest filter) {
        log.info("Generating sales report from {} to {}", filter.getStartDate(), filter.getEndDate());

        var invoices = invoiceRepository.findByDateRange(filter.getStartDate(), filter.getEndDate());

        if (filter.getBranchId() != null) {
            invoices = invoices.stream()
                    .filter(inv -> inv.getBranch().getId().equals(filter.getBranchId()))
                    .collect(Collectors.toList());
        }

        BigDecimal totalSales = invoices.stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.PAID)
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTax = invoices.stream()
                .map(Invoice::getTaxAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDiscount = invoices.stream()
                .map(Invoice::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netRevenue = totalSales.subtract(totalDiscount);

        Long totalInvoices = (long) invoices.size();
        Long totalCustomers = invoices.stream()
                .filter(inv -> inv.getCustomer() != null)
                .map(inv -> inv.getCustomer().getId())
                .distinct()
                .count();

        // Sales by branch
        Map<String, BigDecimal> salesByBranch = invoices.stream()
                .collect(Collectors.groupingBy(
                        inv -> inv.getBranch().getName(),
                        Collectors.reducing(BigDecimal.ZERO, Invoice::getTotalAmount, BigDecimal::add)
                ));

        // Sales by payment method (simplified - would need to query payments)
        Map<String, BigDecimal> salesByPaymentMethod = new HashMap<>();

        return SalesReportResponse.builder()
                .startDate(filter.getStartDate())
                .endDate(filter.getEndDate())
                .totalSales(totalSales)
                .totalTax(totalTax)
                .totalDiscount(totalDiscount)
                .netRevenue(netRevenue)
                .totalInvoices(totalInvoices)
                .totalCustomers(totalCustomers)
                .salesByBranch(salesByBranch)
                .salesByPaymentMethod(salesByPaymentMethod)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductPerformanceDTO> getTopSellingProducts(ReportFilterRequest filter, int limit) {
        return invoiceItemRepository
                .findTopSellingProducts(filter.getStartDate(), filter.getEndDate())
                .stream()
                .limit(limit)
                .map(row -> ProductPerformanceDTO.builder()
                        .productId((Long) row[0])
                        .productName((String) row[1])
                        .totalQuantitySold(((Number) row[2]).intValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchPerformanceResponse> getBranchPerformance(ReportFilterRequest filter) {
        var branches = branchRepository.findAllActiveBranches();

        return branches.stream()
                .map(branch -> {
                    var invoices = invoiceRepository.findByBranchAndDateRange(
                            branch.getId(), filter.getStartDate(), filter.getEndDate());

                    BigDecimal totalSales = invoices.stream()
                            .filter(inv -> inv.getStatus() == InvoiceStatus.PAID)
                            .map(Invoice::getTotalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    Long invoiceCount = (long) invoices.size();

                    BigDecimal avgOrderValue = invoiceCount > 0
                            ? totalSales.divide(BigDecimal.valueOf(invoiceCount), 2, java.math.RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return BranchPerformanceResponse.builder()
                            .branchId(branch.getId())
                            .branchName(branch.getName())
                            .city(branch.getCity())
                            .invoiceCount(invoiceCount)
                            .totalSales(totalSales)
                            .averageOrderValue(avgOrderValue)
                            .build();
                })
                .sorted((a, b) -> b.getTotalSales().compareTo(a.getTotalSales()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaxSummaryResponse getTaxSummary(ReportFilterRequest filter) {
        var invoices = invoiceRepository.findByDateRange(filter.getStartDate(), filter.getEndDate());

        BigDecimal totalTax = invoices.stream()
                .map(Invoice::getTaxAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSalesBeforeTax = invoices.stream()
                .map(Invoice::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSalesAfterTax = invoices.stream()
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgTaxRate = totalSalesBeforeTax.compareTo(BigDecimal.ZERO) > 0
                ? totalTax.multiply(new BigDecimal("100"))
                .divide(totalSalesBeforeTax, 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return TaxSummaryResponse.builder()
                .startDate(filter.getStartDate())
                .endDate(filter.getEndDate())
                .totalTaxCollected(totalTax)
                .totalSalesBeforeTax(totalSalesBeforeTax)
                .totalSalesAfterTax(totalSalesAfterTax)
                .averageTaxRate(avgTaxRate)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountSummaryDTO> getDiscountSummary(ReportFilterRequest filter) {
        var startDateTime = filter.getStartDate().atStartOfDay();
        var endDateTime = filter.getEndDate().atTime(23, 59, 59);

        return discountRepository.getDiscountSummaryByType(startDateTime, endDateTime)
                .stream()
                .map(row -> {
                    var discountType = (DiscountType) row[0];
                    var totalAmount = (BigDecimal) row[1];
                    var usageCount = ((Number) row[2]).longValue();
                    var avgDiscount = usageCount > 0
                            ? totalAmount.divide(BigDecimal.valueOf(usageCount), 2, java.math.RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return DiscountSummaryDTO.builder()
                            .discountType(discountType)
                            .totalAmount(totalAmount)
                            .usageCount(usageCount)
                            .averageDiscount(avgDiscount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LowStockAlertResponse getLowStockAlert(Long branchId) {
        var branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        var lowStockItems = branchStockRepository.findLowStockByBranch(branchId)
                .stream()
                .map(stock -> LowStockItem.builder()
                        .productId(stock.getProduct().getId())
                        .productName(stock.getProduct().getName())
                        .sku(stock.getProduct().getSku())
                        .currentQuantity(stock.getQuantity())
                        .lowStockThreshold(stock.getProduct().getLowStockThreshold())
                        .quantityNeeded(stock.getProduct().getLowStockThreshold() - stock.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return LowStockAlertResponse.builder()
                .branchId(branchId)
                .branchName(branch.getName())
                .lowStockItems(lowStockItems)
                .totalLowStockItems(lowStockItems.size())
                .build();
    }
}