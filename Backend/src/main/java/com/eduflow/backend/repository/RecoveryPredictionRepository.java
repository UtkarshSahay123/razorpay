package com.eduflow.backend.repository;

import com.eduflow.backend.model.RecoveryPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RecoveryPredictionRepository extends JpaRepository<RecoveryPrediction, Long> {
    Optional<RecoveryPrediction> findByPaymentOrderId(Long paymentOrderId);
}
