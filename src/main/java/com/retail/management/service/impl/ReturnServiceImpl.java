package com.retail.management.service.impl;

import com.retail.management.dto.request.returnDto.CreateReturnRequest;
import com.retail.management.dto.request.returnDto.ReturnItemRequest;
import com.retail.management.dto.response.returnDto.ReturnResponse;
import com.retail.management.entity.*;
import com.retail.management.enums.ReturnStatus;
import com.retail.management.exception.BusinessException;
import com.retail.management.exception.ResourceNotFoundException;
import com.retail.management.mapper.ReturnMapper;
import com.retail.management.repository.*;
import com.retail.management.security.SecurityUtils;
import com.retail.management.service.ReturnService;
import com.retail.management.util.CalculationUtil;
import com.retail.management.util.InvoiceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReturnServiceImpl implements ReturnService {

    private final ReturnRepository returnRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BranchStockRepository branchStockRepository;
    private final ReturnMapper returnMapper;

    @Override
    @Transactional
    public ReturnResponse createReturn(CreateReturnRequest request) {
        log.info("Creating return for invoice: {}", request.getInvoiceId());

        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        // Check if return already exists
        if (returnRepository.findByInvoiceId(invoice.getId()).isPresent()) {
            throw new BusinessException("Return already exists for this invoice");
        }

        String username = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new ResourceNotFoundException("User not authenticated"));
        User processedBy = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create return
        Return returnRecord = Return.builder()
                .invoice(invoice)
                .branch(invoice.getBranch())
                .processedBy(processedBy)
                .returnDate(LocalDate.now())
                .returnReason(request.getReturnReason())
                .notes(request.getNotes())
                .status(ReturnStatus.PENDING)
                .totalReturnAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        // Generate return number
        Long sequence = returnRepository.count() + 1;
        returnRecord.setReturnNumber(
                InvoiceNumberGenerator.generateReturnNumber(invoice.getBranch().getCode(), sequence)
        );

        // Create return items and calculate total
        BigDecimal totalReturnAmount = BigDecimal.ZERO;

        for (ReturnItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            // Find corresponding invoice item
            InvoiceItem invoiceItem = invoice.getItems().stream()
                    .filter(item -> item.getProduct().getId().equals(itemRequest.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(
                            "Product not found in original invoice"
                    ));

            // Validate quantity
            if (itemRequest.getQuantity() > invoiceItem.getQuantity()) {
                throw new BusinessException(
                        String.format("Return quantity (%d) exceeds invoiced quantity (%d)",
                                itemRequest.getQuantity(), invoiceItem.getQuantity())
                );
            }

            BigDecimal returnAmount = CalculationUtil.calculateLineTotal(
                    itemRequest.getQuantity(),
                    invoiceItem.getUnitPrice()
            );

            ReturnItem returnItem = ReturnItem.builder()
                    .returnRecord(returnRecord)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(invoiceItem.getUnitPrice())
                    .returnAmount(returnAmount)
                    .condition(itemRequest.getCondition())
                    .notes(itemRequest.getNotes())
                    .build();

            returnRecord.getItems().add(returnItem);
            totalReturnAmount = totalReturnAmount.add(returnAmount);
        }

        returnRecord.setTotalReturnAmount(totalReturnAmount);
        returnRecord = returnRepository.save(returnRecord);

        log.info("Return created successfully: {}", returnRecord.getReturnNumber());
        return returnMapper.toResponse(returnRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public ReturnResponse getReturnById(Long id) {
        Return returnRecord = returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Return", "id", id));
        return returnMapper.toResponse(returnRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReturnResponse> getAllReturns(Pageable pageable) {
        return returnRepository.findAll(pageable)
                .map(returnMapper::toResponse);
    }

    @Override
    @Transactional
    public ReturnResponse approveReturn(Long id) {
        log.info("Approving return: {}", id);

        Return returnRecord = returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Return", "id", id));

        if (returnRecord.getStatus() != ReturnStatus.PENDING) {
            throw new BusinessException("Only pending returns can be approved");
        }

        // Restore stock for each item
        for (ReturnItem item : returnRecord.getItems()) {
            BranchStock stock = branchStockRepository
                    .findByBranchIdAndProductId(
                            returnRecord.getBranch().getId(),
                            item.getProduct().getId()
                    )
                    .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

            stock.setQuantity(stock.getQuantity() + item.getQuantity());
            branchStockRepository.save(stock);
        }

        returnRecord.setStatus(ReturnStatus.COMPLETED);
        returnRecord = returnRepository.save(returnRecord);

        log.info("Return approved and stock restored: {}", returnRecord.getReturnNumber());
        return returnMapper.toResponse(returnRecord);
    }

    @Override
    @Transactional
    public ReturnResponse rejectReturn(Long id, String reason) {
        log.info("Rejecting return: {}", id);

        Return returnRecord = returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Return", "id", id));

        if (returnRecord.getStatus() != ReturnStatus.PENDING) {
            throw new BusinessException("Only pending returns can be rejected");
        }

        returnRecord.setStatus(ReturnStatus.REJECTED);
        returnRecord.setNotes(returnRecord.getNotes() + "\nRejection reason: " + reason);
        returnRecord = returnRepository.save(returnRecord);

        log.info("Return rejected: {}", returnRecord.getReturnNumber());
        return returnMapper.toResponse(returnRecord);
    }
}