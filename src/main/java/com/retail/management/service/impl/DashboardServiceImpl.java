package com.retail.management.service.impl;

import com.retail.management.dto.response.stats.BranchStatsResponse;
import com.retail.management.dto.response.stats.DashboardStatsResponse;
import com.retail.management.entity.BranchStock;
import com.retail.management.entity.Invoice;
import com.retail.management.entity.User;
import com.retail.management.enums.InvoiceStatus;
import com.retail.management.enums.ReturnStatus;
import com.retail.management.exception.ResourceNotFoundException;
import com.retail.management.repository.*;
import com.retail.management.service.DashboardService;
import com.retail.management.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final InvoiceRepository invoiceRepository;
    private final BranchStockRepository branchStockRepository;
    private final CustomerRepository customerRepository;
    private final ReturnRepository returnRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        log.info("Generating dashboard statistics");

        LocalDate today = LocalDate.now();
        LocalDate weekStart = DateUtil.getStartOfWeek(today);
        LocalDate monthStart = DateUtil.getStartOfMonth(today);

        // Today's sales
        var todayInvoices = invoiceRepository.findByDateRange(today, today);
        BigDecimal todaySales = todayInvoices.stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.PAID)
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Week sales
        var weekInvoices = invoiceRepository.findByDateRange(weekStart, today);
        BigDecimal weekSales = weekInvoices.stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.PAID)
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Month sales
        var monthInvoices = invoiceRepository.findByDateRange(monthStart, today);
        BigDecimal monthSales = monthInvoices.stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.PAID)
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Invoice counts
        Long todayInvoiceCount = (long) todayInvoices.size();
        Long weekInvoiceCount = (long) weekInvoices.size();
        Long monthInvoiceCount = (long) monthInvoices.size();

        // Stock alerts
        var allLowStock = branchStockRepository.findAll().stream()
                .filter(stock -> stock.getQuantity() < stock.getProduct().getLowStockThreshold())
                .count();

        var outOfStock = branchStockRepository.findOutOfStockItems().size();

        // Active customers
        Long activeCustomers = customerRepository.findAllRegisteredCustomers().stream()
                .filter(c -> c.getDeletedAt() == null)
                .count();

        // Pending returns
        Long pendingReturns = (long) returnRepository.findByStatus(ReturnStatus.PENDING).size();

        return DashboardStatsResponse.builder()
                .todaySales(todaySales)
                .weekSales(weekSales)
                .monthSales(monthSales)
                .todayInvoices(todayInvoiceCount)
                .weekInvoices(weekInvoiceCount)
                .monthInvoices(monthInvoiceCount)
                .lowStockProducts((int) allLowStock)
                .outOfStockProducts(outOfStock)
                .activeCustomers(activeCustomers)
                .pendingReturns(pendingReturns)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BranchStatsResponse getBranchStats(Long branchId) {
        log.info("Generating statistics for branch: {}", branchId);

        var branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        LocalDate today = LocalDate.now();

        // Branch stock
        var branchStocks = branchStockRepository.findByBranchId(branchId);
        Integer totalProducts = branchStocks.size();
        Integer totalStock = branchStocks.stream()
                .mapToInt(BranchStock::getQuantity)
                .sum();

        Integer lowStockItems = (int) branchStocks.stream()
                .filter(stock -> stock.getQuantity() < stock.getProduct().getLowStockThreshold())
                .count();

        // Today's invoices and sales
        Long todayInvoiceCount = invoiceRepository.getInvoiceCountByBranchAndDate(branchId, today);
        BigDecimal todaySales = invoiceRepository.getTotalSalesByBranchAndDate(branchId, today);

        // Active users
        Long activeUsers = userRepository.findActiveUsersByBranch(branchId).stream()
                .filter(User::getActive)
                .count();

        return BranchStatsResponse.builder()
                .branchId(branchId)
                .branchName(branch.getName())
                .totalProducts(totalProducts)
                .totalStock(totalStock)
                .lowStockItems(lowStockItems)
                .todayInvoices(todayInvoiceCount)
                .todaySales(todaySales != null ? todaySales : BigDecimal.ZERO)
                .activeUsers(activeUsers)
                .build();
    }
}
