package com.retail.management.service;

import com.retail.management.dto.request.product.CreateProductRequest;
import com.retail.management.dto.request.product.UpdateProductRequest;
import com.retail.management.dto.request.search.ProductSearchCriteria;
import com.retail.management.dto.response.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);
    ProductResponse getProductById(Long id);
    Page<ProductResponse> getAllProducts(Pageable pageable);
    Page<ProductResponse> searchProducts(ProductSearchCriteria criteria, Pageable pageable);
    ProductResponse updateProduct(Long id, UpdateProductRequest request);
    ProductResponse updateProductImages(Long productId, String imageUrl, String thumbnailUrl);
    void deleteProduct(Long id);
    List<String> getAllCategories();
}