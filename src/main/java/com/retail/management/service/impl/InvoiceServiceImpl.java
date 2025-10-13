package com.retail.management.service.impl;

import com.retail.management.dto.request.invoice.ApplyDiscountRequest;
import com.retail.management.dto.request.invoice.CreateInvoiceRequest;
import com.retail.management.dto.request.invoice.InvoiceItemRequest;
import com.retail.management.dto.request.search.InvoiceFilterRequest;
import com.retail.management.dto.response.invoice.DiscountResponse;
import com.retail.management.dto.response.invoice.InvoiceDetailResponse;
import com.retail.management.dto.response.invoice.InvoiceResponse;
import com.retail.management.dto.response.payment.PaymentResponse;
import com.retail.management.entity.*;
import com.retail.management.enums.DiscountType;
import com.retail.management.enums.InvoiceStatus;
import com.retail.management.exception.BusinessException;
import com.retail.management.exception.ResourceNotFoundException;
import com.retail.management.mapper.DiscountMapper;
import com.retail.management.mapper.InvoiceMapper;
import com.retail.management.mapper.PaymentMapper;
import com.retail.management.repository.*;
import com.retail.management.repository.specification.InvoiceSpecification;
import com.retail.management.security.SecurityUtils;
import com.retail.management.service.InvoiceService;
import com.retail.management.util.CalculationUtil;
import com.retail.management.util.InvoiceNumberGenerator;
import com.retail.management.validator.PromoCodeValidator;
import com.retail.management.validator.StockValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final BranchRepository branchRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BranchStockRepository branchStockRepository;
    private final PaymentRepository paymentRepository;
    private final DiscountRepository discountRepository;
    private final InvoiceMapper invoiceMapper;
    private final PaymentMapper paymentMapper;
    private final DiscountMapper discountMapper;
    private final StockValidator stockValidator;
    private final PromoCodeValidator promoCodeValidator;

    @Override
    @Transactional
    public InvoiceResponse createInvoice(CreateInvoiceRequest request) {
        log.info("Creating invoice for branch: {}", request.getBranchId());

        // Check idempotency
        if (request.getIdempotencyKey() != null) {
            Optional<Invoice> existing = invoiceRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                log.info("Invoice already exists with idempotency key: {}", request.getIdempotencyKey());
                return invoiceMapper.toResponse(existing.get());
            }
        }

        // Get branch
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        // Get cashier
        String username = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new ResourceNotFoundException("User not authenticated"));
        User cashier = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get customer if provided
        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        }

        // Validate stock availability for all items
        for (InvoiceItemRequest itemRequest : request.getItems()) {
            stockValidator.validateStockAvailability(
                    request.getBranchId(),
                    itemRequest.getProductId(),
                    itemRequest.getQuantity()
            );
        }

        // Create invoice
        Invoice invoice = Invoice.builder()
                .branch(branch)
                .customer(customer)
                .cashier(cashier)
                .customerType(request.getCustomerType() != null ? request.getCustomerType() : "WALK_IN")
                .invoiceDate(LocalDate.now())
                .status(InvoiceStatus.PENDING)
                .notes(request.getNotes())
                .idempotencyKey(request.getIdempotencyKey())
                .subtotal(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .paidAmount(BigDecimal.ZERO)
                .outstandingBalance(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        // Generate invoice number
        Long sequence = invoiceRepository.count() + 1;
        invoice.setInvoiceNumber(InvoiceNumberGenerator.generate(branch.getCode(), sequence));

        // Create invoice items and calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (InvoiceItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            // Calculate line total
            BigDecimal unitPrice = product.getPrice();
            BigDecimal lineSubtotal = CalculationUtil.calculateLineTotal(itemRequest.getQuantity(), unitPrice);
            BigDecimal lineTax = CalculationUtil.calculateTax(lineSubtotal, product.getTaxRate());
            BigDecimal lineTotal = lineSubtotal.add(lineTax);

            // Create invoice item
            InvoiceItem item = InvoiceItem.builder()
                    .invoice(invoice)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .taxRate(product.getTaxRate())
                    .taxAmount(lineTax)
                    .discountAmount(BigDecimal.ZERO)
                    .lineTotal(lineTotal)
                    .build();

            invoice.getItems().add(item);

            subtotal = subtotal.add(lineSubtotal);
            totalTax = totalTax.add(lineTax);

            // Reduce stock
            BranchStock stock = branchStockRepository
                    .findByBranchIdAndProductId(request.getBranchId(), product.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

            stock.setQuantity(stock.getQuantity() - itemRequest.getQuantity());
            branchStockRepository.save(stock);
        }

        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(totalTax);

        // Apply discounts if provided
        BigDecimal totalDiscount = BigDecimal.ZERO;
        if (request.getDiscounts() != null && !request.getDiscounts().isEmpty()) {
            for (ApplyDiscountRequest discountRequest : request.getDiscounts()) {
                BigDecimal discountAmount = calculateDiscount(invoice, discountRequest);
                totalDiscount = totalDiscount.add(discountAmount);

                // Create discount record
                Discount discount = Discount.builder()
                        .invoice(invoice)
                        .discountType(discountRequest.getDiscountType())
                        .discountValue(discountRequest.getDiscountValue())
                        .discountAmount(discountAmount)
                        .description(discountRequest.getDescription())
                        .appliedBy(username)
                        .build();

                if (discountRequest.getDiscountType() == DiscountType.PROMO_CODE) {
                    PromoCode promoCode = promoCodeValidator.validatePromoCode(discountRequest.getPromoCode());
                    discount.setPromoCode(promoCode);
                    promoCode.setTimesUsed(promoCode.getTimesUsed() + 1);
                }

                discountRepository.save(discount);
            }
        }

        invoice.setDiscountAmount(totalDiscount);

        // Calculate final total
        BigDecimal totalAmount = subtotal.add(totalTax).subtract(totalDiscount);
        invoice.setTotalAmount(totalAmount);
        invoice.setOutstandingBalance(totalAmount);

        invoice = invoiceRepository.save(invoice);
        log.info("Invoice created successfully: {}", invoice.getInvoiceNumber());

        return invoiceMapper.toResponse(invoice);
    }

    private BigDecimal calculateDiscount(Invoice invoice, ApplyDiscountRequest request) {
        BigDecimal amount = invoice.getSubtotal().add(invoice.getTaxAmount());

        switch (request.getDiscountType()) {
            case FIXED_AMOUNT:
                return CalculationUtil.calculateFixedDiscount(amount, request.getDiscountValue());

            case PERCENTAGE:
                return CalculationUtil.calculatePercentageDiscount(amount, request.getDiscountValue());

            case PROMO_CODE:
                PromoCode promoCode = promoCodeValidator.validatePromoCode(request.getPromoCode());
                if (promoCode.getDiscountType() == DiscountType.PERCENTAGE) {
                    return CalculationUtil.calculatePercentageDiscountWithMax(
                            amount,
                            promoCode.getDiscountValue(),
                            promoCode.getMaxDiscountAmount()
                    );
                } else {
                    return CalculationUtil.calculateFixedDiscount(amount, promoCode.getDiscountValue());
                }

            default:
                return BigDecimal.ZERO;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDetailResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));

        List<PaymentResponse> payments = paymentRepository.findByInvoiceId(id)
                .stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());

        List<DiscountResponse> discounts = discountRepository.findByInvoiceId(id)
                .stream()
                .map(discountMapper::toResponse)
                .collect(Collectors.toList());

        return InvoiceDetailResponse.builder()
                .invoice(invoiceMapper.toResponse(invoice))
                .payments(payments)
                .discounts(discounts)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable)
                .map(invoiceMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceResponse> filterInvoices(InvoiceFilterRequest filter, Pageable pageable) {
        Specification<Invoice> spec = InvoiceSpecification.filterInvoices(
                filter.getBranchId(),
                filter.getCustomerId(),
                filter.getCashierId(),
                filter.getStatus(),
                filter.getStartDate(),
                filter.getEndDate(),
                filter.getMinAmount(),
                filter.getMaxAmount(),
                filter.getCustomerType()
        );

        return invoiceRepository.findAll(spec, pageable)
                .map(invoiceMapper::toResponse);
    }

    @Override
    @Transactional
    public InvoiceResponse cancelInvoice(Long id) {
        log.info("Cancelling invoice: {}", id);

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));

        if (!invoice.getStatus().canBeCancelled()) {
            throw new BusinessException("Invoice cannot be cancelled. Current status: " + invoice.getStatus());
        }

        if (invoice.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("Cannot cancel invoice with payments. Please process a refund instead.");
        }

        // Restore stock
        for (InvoiceItem item : invoice.getItems()) {
            BranchStock stock = branchStockRepository
                    .findByBranchIdAndProductId(invoice.getBranch().getId(), item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

            stock.setQuantity(stock.getQuantity() + item.getQuantity());
            branchStockRepository.save(stock);
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoice = invoiceRepository.save(invoice);

        log.info("Invoice cancelled successfully: {}", invoice.getInvoiceNumber());
        return invoiceMapper.toResponse(invoice);
    }
}