package com.study.ecommerce.domain.cart.repository;

import com.study.ecommerce.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByMemberId(Long id);

    boolean existsByMemberId(Long memberId);
}
