package com.study.ecommerce.domain.product.service;

import com.study.ecommerce.domain.category.repository.CategoryRepository;
import com.study.ecommerce.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /**
     *  카테고리별 판매중인 상품을 전체조회
     *  List<ProductResponse> getActiveProductsByCategory
     *  param Long categoryId
     */

    /**
     * 페이징 모든 판매중인 상품 조회 -> pageable
     * Page<ProductResponse> getAllActiveProducts
     */

    /**
     * 판매중인 상품 상세조회 -> id, ProductResponse
     */
}
