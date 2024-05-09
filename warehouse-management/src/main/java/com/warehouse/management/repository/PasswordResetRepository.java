package com.warehouse.management.repository;

import com.warehouse.management.model.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    PasswordReset findByUsername(String username);
}
