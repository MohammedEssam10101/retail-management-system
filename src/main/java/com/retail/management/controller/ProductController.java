package com.retail.management.controller;

import com.retail.management.dto.request.product.CreateProductRequest;
import com.retail.management.dto.request.product.UpdateProductRequest;
import com.retail.management.dto.request.search.ProductSearchCriteria;
import com.retail.management.dto.response.ApiResponse;
import com.retail.management.dto.response.product.ProductResponse;
//import com.retail.management.service.FileStorageService;
import com.retail.management.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {

    private final ProductService productService;
//    private final FileStorageService fileStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create product", description = "Create a new product with optional image")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestPart("product") CreateProductRequest request) {
//            @RequestPart(value = "image", required = false) MultipartFile image) {

        ProductResponse product = productService.createProduct(request);

//        // Upload image if provided
//        if (image != null && !image.isEmpty()) {
//            String imageUrl = fileStorageService.storeProductImage(image, product.getId());
//            String thumbnailUrl = fileStorageService.generateThumbnail(imageUrl, 200, 200);
//            product = productService.updateProductImages(product.getId(), imageUrl, thumbnailUrl);
//        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(product, "Product created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get product by ID", description = "Retrieve product details by ID")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all products", description = "Retrieve all products with pagination")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Search products", description = "Search products with criteria")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProducts(
            ProductSearchCriteria criteria,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> products = productService.searchProducts(criteria, pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/categories")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get categories", description = "Retrieve all product categories")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategories() {
        List<String> categories = productService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update product", description = "Update product details")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success(product, "Product updated successfully"));
    }

//    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
//    @Operation(summary = "Update product image", description = "Update product image")
//    public ResponseEntity<ApiResponse<ProductResponse>> updateProductImage(
//            @PathVariable Long id,
//            @RequestPart("image") MultipartFile image) {
//
//        // Delete old image
//        ProductResponse existingProduct = productService.getProductById(id);
//        if (existingProduct.getImageUrl() != null) {
//            fileStorageService.deleteFile(existingProduct.getImageUrl());
//        }
//
//        // Upload new image
//        String imageUrl = fileStorageService.storeProductImage(image, id);
//        String thumbnailUrl = fileStorageService.generateThumbnail(imageUrl, 200, 200);
//
//        ProductResponse product = productService.updateProductImages(id, imageUrl, thumbnailUrl);
//        return ResponseEntity.ok(ApiResponse.success(product, "Product image updated successfully"));
//    }

//    @DeleteMapping("/{id}/image")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
//    @Operation(summary = "Delete product image", description = "Delete product image")
//    public ResponseEntity<ApiResponse<ProductResponse>> deleteProductImage(@PathVariable Long id) {
//        ProductResponse product = productService.getProductById(id);
//
//        if (product.getImageUrl() != null) {
//            fileStorageService.deleteFile(product.getImageUrl());
//        }
//
//        ProductResponse updatedProduct = productService.updateProductImages(id, null, null);
//        return ResponseEntity.ok(ApiResponse.success(updatedProduct, "Product image deleted successfully"));
//    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete product", description = "Soft delete product")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
    }
}
