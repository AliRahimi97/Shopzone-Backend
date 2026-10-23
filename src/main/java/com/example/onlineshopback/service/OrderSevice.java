package com.example.onlineshopback.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.onlineshopback.dto.CheckoutRequest;
import com.example.onlineshopback.dto.response.OrderItemResponse;
import com.example.onlineshopback.dto.response.OrderResponse;
import com.example.onlineshopback.model.CartItem;
import com.example.onlineshopback.model.OrderItem;
import com.example.onlineshopback.model.OrderShop;
import com.example.onlineshopback.model.OrderStatus;
import com.example.onlineshopback.model.Payment;
import com.example.onlineshopback.model.PaymentStatus;
import com.example.onlineshopback.model.Product;
import com.example.onlineshopback.model.ProductImage;
import com.example.onlineshopback.model.Users;
import com.example.onlineshopback.repository.CartItemRepository;
import com.example.onlineshopback.repository.OrderRepository;
import com.example.onlineshopback.repository.PaymentRepository;
import com.example.onlineshopback.repository.ProductRepository;
import com.example.onlineshopback.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderSevice {
    
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderShopRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public OrderResponse checkout(Integer userId, CheckoutRequest request){
        Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User Not Found"));
        List<CartItem> cartItems = cartItemRepository.findByUserIdWithProduct(userId);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
      
        }

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for(CartItem cartItem: cartItems){
            Product product = cartItem.getProduct();

            if (product.getActive() != null && !product.getActive()) {
                throw new RuntimeException("Product is not active: " + product.getName());
            }

            int qty = cartItem.getQuantity();
            if (product.getStock() == null || product.getStock() < qty) {
                throw new RuntimeException("Not enough stock for: " + product.getName());
            }   
            BigDecimal unitPrice = calcFinalPrice(product);
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty));
            total = total.add(lineTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(qty);
            orderItem.setPrice(unitPrice); // snapshot
            orderItems.add(orderItem);

            // reduce stock
            product.setStock(product.getStock() - qty);
            productRepository.save(product);
        }
        OrderShop order = new OrderShop();
        order.setUsers(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(total.setScale(2, RoundingMode.HALF_UP));
        order.setOrderItems(new ArrayList<>());

        for (OrderItem oi : orderItems) {
            oi.setOrder(order);
            order.getOrderItems().add(oi);
        }

        OrderShop savedOrder = orderShopRepository.save(order);
        // Payment (placeholder – mark SUCCESS for demo, or PENDING)
        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setAmount(total.doubleValue()); // your entity uses Double
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.SUCCESS); // or PENDING
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);

        // If paid immediately:
        savedOrder.setStatus(OrderStatus.PAID);
        orderShopRepository.save(savedOrder);
        // Clear cart
        cartItemRepository.deleteByUsersUserId(userId);

        return toOrderResponse(savedOrder, payment);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(Integer userId) {
        List<OrderShop> orders = orderShopRepository.findByUserIdWithItems(userId);
        List<OrderResponse> result = new ArrayList<>();
        for (OrderShop o : orders) {
            Payment payment = paymentRepository.findByOrderOrderId(o.getOrderId()).orElse(null);
            result.add(toOrderResponse(o, payment));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Integer userId, Long orderId) {
        OrderShop order = orderShopRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUsers().getUserId().equals(userId)) {
            throw new RuntimeException("Order not found");
        }

        Payment payment = paymentRepository.findByOrderOrderId(orderId).orElse(null);
        return toOrderResponse(order, payment);
    }

    private BigDecimal calcFinalPrice(Product p) {
        double price = p.getPrice() != null ? p.getPrice() : 0;
        int discount = p.getDiscountPercent() != null ? p.getDiscountPercent() : 0;
        double finalPrice = price * (1 - discount / 100.0);
        return BigDecimal.valueOf(finalPrice).setScale(2, RoundingMode.HALF_UP);
    }

    private OrderResponse toOrderResponse(OrderShop order, Payment payment) {
        List<OrderItemResponse> items = new ArrayList<>();
        if (order.getOrderItems() != null) {
            for (OrderItem oi : order.getOrderItems()) {
                Product p = oi.getProduct();
                BigDecimal line = oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity()));
                items.add(OrderItemResponse.builder()
                        .orderItemId(oi.getOrderItemId())
                        .productId(p != null ? p.getProductId() : null)
                        .productName(p != null ? p.getName() : null)
                        .imageUrl(resolveImageUrl(p))
                        .quantity(oi.getQuantity())
                        .price(oi.getPrice())
                        .lineTotal(line.setScale(2, RoundingMode.HALF_UP))
                        .build());
            }
        }

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .totalAmount(order.getTotalAmount())
                .userId(order.getUsers() != null ? order.getUsers().getUserId() : null)
                .userEmail(order.getUsers() != null ? order.getUsers().getEmail() : null)
                .items(items)
                .paymentMethod(payment != null ? payment.getPaymentMethod() : null)
                .paymentStatus(payment != null && payment.getPaymentStatus() != null
                        ? payment.getPaymentStatus().name()
                        : null)
                .build();
    }

    private String resolveImageUrl(Product p) {
        if (p == null) return null;
        if (p.getImages() != null && !p.getImages().isEmpty()) {
            return p.getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getPrimaryImage()))
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse(p.getImages().get(0).getImageUrl());
        }
        return p.getImageUrl();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderShopRepository.findAllWithItems().stream()
                .map(o -> {
                    Payment payment = paymentRepository
                            .findByOrderOrderId(o.getOrderId())
                            .orElse(null);
                    return toOrderResponse(o, payment);
                })
                .toList();
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus status) {
        OrderShop order = orderShopRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        orderShopRepository.save(order);

        Payment payment = paymentRepository
                .findByOrderOrderId(orderId)
                .orElse(null);

        return toOrderResponse(order, payment);
    }


}
