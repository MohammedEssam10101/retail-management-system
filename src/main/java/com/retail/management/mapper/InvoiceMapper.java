package com.retail.management.mapper;

import com.retail.management.dto.response.invoice.InvoiceItemResponse;
import com.retail.management.dto.response.invoice.InvoiceResponse;
import com.retail.management.entity.Invoice;
import com.retail.management.entity.InvoiceItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvoiceMapper {

    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.name")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", expression = "java(getCustomerFullName(invoice))")
    @Mapping(target = "cashierId", source = "cashier.id")
    @Mapping(target = "cashierName", expression = "java(getCashierFullName(invoice))")
    @Mapping(target = "items", source = "items")
    InvoiceResponse toResponse(Invoice invoice);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productSku", source = "product.sku")
    InvoiceItemResponse toItemResponse(InvoiceItem item);

    default String getCustomerFullName(Invoice invoice) {
        if (invoice.getCustomer() != null) {
            return invoice.getCustomer().getFirstName() + " " + invoice.getCustomer().getLastName();
        }
        return "Walk-in Customer";
    }

    default String getCashierFullName(Invoice invoice) {
        if (invoice.getCashier() != null) {
            return invoice.getCashier().getFirstName() + " " + invoice.getCashier().getLastName();
        }
        return null;
    }
}