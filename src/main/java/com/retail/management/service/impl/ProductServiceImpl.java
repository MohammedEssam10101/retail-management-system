package com.retail.management.service.impl;

import com.retail.management.dto.request.product.CreateProductRequest;
import com.retail.management.dto.request.product.UpdateProductRequest;
import com.retail.management.dto.request.search.ProductSearchCriteria;
import com.retail.management.dto.response.product.ProductResponse;
import com.retail.management.entity.Product;
import com.retail.management.exception.DuplicateResourceException;
import com.retail.management.exception.ResourceNotFoundException;
import com.retail.management.mapper.ProductMapper;
import com.retail.management.repository.ProductRepository;
import com.retail.management.repository.specification.ProductSpecification;
import com.retail.management.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        log.info("Creating product: {}", request.getName());

        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Product", "SKU", request.getSku());
        }

        Product product = productMapper.toEntity(request);
        product.setActive(true);

        product = productRepository.save(product);
        log.info("Product created successfully: {}", product.getName());

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAllActiveProducts(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(ProductSearchCriteria criteria, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.filterProducts(
                criteria.getSearchTerm(),
                criteria.getCategory(),
                criteria.getMinPrice(),
                criteria.getMaxPrice(),
                criteria.getActive()
        );

        return productRepository.findAll(spec, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        log.info("Updating product: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        productMapper.updateEntityFromRequest(request, product);

        product = productRepository.save(product);
        log.info("Product updated successfully: {}", product.getName());

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public ProductResponse updateProductImages(Long productId, String imageUrl, String thumbnailUrl) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

//        product.setImageUrl(imageUrl);
//        product.setThumbnailUrl(thumbnailUrl);

        product = productRepository.save(product);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        log.info("Soft deleting product: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setDeletedAt(LocalDateTime.now());
        product.setActive(false);
        productRepository.save(product);

        log.info("Product soft deleted: {}", product.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }
}