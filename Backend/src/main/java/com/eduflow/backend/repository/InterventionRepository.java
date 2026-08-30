package com.eduflow.backend.repository;

import com.eduflow.backend.model.Intervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterventionRepository extends JpaRepository<Intervention, Long> {
    List<Intervention> findByPaymentOrderId(Long paymentOrderId);
    List<Intervention> findByStatus(String status);
    List<Intervention> findTop10ByOrderByIdDesc();
    List<Intervention> findByPaymentOrder(com.eduflow.backend.model.PaymentOrder paymentOrder);
}
