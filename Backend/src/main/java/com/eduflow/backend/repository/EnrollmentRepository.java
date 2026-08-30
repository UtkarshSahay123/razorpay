package com.eduflow.backend.repository;

import com.eduflow.backend.model.Enrollment;
import com.eduflow.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUser(User user);
    long countByUserAndCompleted(User user, boolean completed);
}
