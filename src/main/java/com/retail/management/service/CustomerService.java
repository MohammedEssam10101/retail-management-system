package com.retail.management.service;

import com.retail.management.dto.request.customer.CreateCustomerRequest;
import com.retail.management.dto.request.customer.UpdateCustomerRequest;
import com.retail.management.dto.request.search.CustomerSearchCriteria;
import com.retail.management.dto.response.customer.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    CustomerResponse createCustomer(CreateCustomerRequest request);
    CustomerResponse getCustomerById(Long id);
    Page<CustomerResponse> getAllCustomers(Pageable pageable);
    Page<CustomerResponse> searchCustomers(CustomerSearchCriteria criteria, Pageable pageable);
    CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request);
    void deleteCustomer(Long id);
}