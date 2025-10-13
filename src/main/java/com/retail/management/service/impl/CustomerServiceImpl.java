package com.retail.management.service.impl;

import com.retail.management.dto.request.customer.CreateCustomerRequest;
import com.retail.management.dto.request.customer.UpdateCustomerRequest;
import com.retail.management.dto.request.search.CustomerSearchCriteria;
import com.retail.management.dto.response.customer.CustomerResponse;
import com.retail.management.entity.Customer;
import com.retail.management.exception.DuplicateResourceException;
import com.retail.management.exception.ResourceNotFoundException;
import com.retail.management.mapper.CustomerMapper;
import com.retail.management.repository.CustomerRepository;
import com.retail.management.repository.specification.CustomerSpecification;
import com.retail.management.service.CustomerService;
import com.retail.management.util.InvoiceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        log.info("Creating customer: {} {}", request.getFirstName(), request.getLastName());

        // Check if phone exists
        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Customer", "phone", request.getPhone());
        }

        // Check if email exists (if provided)
        if (request.getEmail() != null && customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Customer", "email", request.getEmail());
        }

        Customer customer = customerMapper.toEntity(request);

        // Generate customer code
        Long sequence = customerRepository.count() + 1;
        customer.setCustomerCode(InvoiceNumberGenerator.generateCustomerCode(sequence));

        customer = customerRepository.save(customer);
        log.info("Customer created successfully: {}", customer.getCustomerCode());

        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        return customerRepository.findAllNotDeleted(pageable)
                .map(customerMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> searchCustomers(CustomerSearchCriteria criteria, Pageable pageable) {
        Specification<Customer> spec = CustomerSpecification.filterCustomers(
                criteria.getSearchTerm(),
                criteria.getCity(),
                criteria.getIsWalkIn(),
                criteria.getMinLoyaltyPoints(),
                criteria.getMaxLoyaltyPoints()
        );

        return customerRepository.findAll(spec, pageable)
                .map(customerMapper::toResponse);
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request) {
        log.info("Updating customer: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        // Check phone uniqueness if changed
        if (request.getPhone() != null && !request.getPhone().equals(customer.getPhone())) {
            if (customerRepository.existsByPhone(request.getPhone())) {
                throw new DuplicateResourceException("Customer", "phone", request.getPhone());
            }
        }

        // Check email uniqueness if changed
        if (request.getEmail() != null && !request.getEmail().equals(customer.getEmail())) {
            if (customerRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Customer", "email", request.getEmail());
            }
        }

        customerMapper.updateEntityFromRequest(request, customer);
        customer = customerRepository.save(customer);

        log.info("Customer updated successfully: {}", customer.getCustomerCode());
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Soft deleting customer: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        customer.setDeletedAt(LocalDateTime.now());
        customerRepository.save(customer);

        log.info("Customer soft deleted: {}", customer.getCustomerCode());
    }
}