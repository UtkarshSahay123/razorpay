package com.eduflow.backend.repository;

import com.eduflow.backend.model.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
    Optional<PaymentOrder> findByRazorpayOrderId(String razorpayOrderId);
    Optional<PaymentOrder> findByRazorpayPaymentLinkId(String razorpayPaymentLinkId);
    List<PaymentOrder> findByStatusAndIsRecovered(String status, Boolean isRecovered);
    List<PaymentOrder> findByStatus(String status);
    Optional<PaymentOrder> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}
