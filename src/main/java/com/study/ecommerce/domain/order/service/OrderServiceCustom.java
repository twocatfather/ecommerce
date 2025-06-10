package com.study.ecommerce.domain.order.service;

import com.study.ecommerce.domain.cart.entity.Cart;
import com.study.ecommerce.domain.cart.entity.CartItem;
import com.study.ecommerce.domain.cart.repository.CartItemRepository;
import com.study.ecommerce.domain.cart.repository.CartRepository;
import com.study.ecommerce.domain.member.entity.Member;
import com.study.ecommerce.domain.member.repository.MemberRepository;
import com.study.ecommerce.domain.order.dto.OrderCreateRequest;
import com.study.ecommerce.domain.order.dto.OrderDetailResponse;
import com.study.ecommerce.domain.order.dto.OrderResponse;
import com.study.ecommerce.domain.order.entity.Order;
import com.study.ecommerce.domain.order.entity.OrderItem;
import com.study.ecommerce.domain.order.repository.OrderItemRepository;
import com.study.ecommerce.domain.order.repository.OrderRepository;
import com.study.ecommerce.domain.payment.repository.PaymentRepository;
import com.study.ecommerce.domain.product.entity.Product;
import com.study.ecommerce.domain.product.repository.ProductRepository;
import com.study.ecommerce.global.error.exception.EntityNotFoundException;
import com.study.ecommerce.infra.payment.service.MockPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.study.ecommerce.domain.order.entity.Order.OrderStatus.CREATED;

@Service
@RequiredArgsConstructor
public class OrderServiceCustom implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final PaymentRepository paymentRepository;
    private final MockPaymentService mockPaymentService;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public OrderResponse createOrder(OrderCreateRequest request, String email) {
        // 1. 회원 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 주문 생성(초기 총액자체를 0원으로 설정)
        Order order = Order.builder()
                .memberId(member.getId())
                .status(CREATED)
                .orderDate(LocalDateTime.now())
                .totalAmount(0L)
                .build();

        order = orderRepository.save(order);

        // 3. 주문 상품 처리 및 총액 계산
        long totalAmount = 0L;

        if (request.cartItemIds() != null && !request.cartItemIds().isEmpty()) {
            // 장바구니로 상품을 주문

        }

        return null;
    }

    @Override
    public OrderResponse cancelOrder(Long orderId, String email) {
        return null;
    }

    @Override
    public OrderDetailResponse getOrderDetail(Long orderId, String email) {
        return null;
    }

    @Override
    public Page<OrderResponse> getOrders(String email, Pageable pageable) {
        return null;
    }

    private long processCartItems(Order order, List<Long> cartItemIds, Member member) {
        Cart cart = cartRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new EntityNotFoundException("장바구니를 찾을 수 없습니다."));

        long totalAmount = 0L;

        for (Long cartItemId : cartItemIds) {
            CartItem cartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(() -> new EntityNotFoundException("장바구니 상품을 찾을 수 없습니다." + cartItemId));

            // 해당 장바구니 상품이 현재 사용자의 것인지
            if (!cartItem.getCartId().equals(cart.getId())) {
                throw new IllegalArgumentException("장바구니 상품 접근 권한이 없습니다.");
            }

            Product product = productRepository.findByIdWithPessimisticLock(cartItem.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

            product.decreasesStock(cartItem.getQuantity());
            productRepository.save(product);

            // 주문 상품 추가
            OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getId())
                    .productId(product.getId())
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .build();

            orderItemRepository.save(orderItem);
            totalAmount += orderItem.getTotalPrice();

            // 주문한 상품은 장바구니에서 제거
            cartItemRepository.delete(cartItem);
        }
        return totalAmount;
    }
}
