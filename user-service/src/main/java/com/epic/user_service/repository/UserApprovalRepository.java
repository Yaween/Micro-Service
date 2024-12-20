package com.epic.user_service.repository;

import com.epic.user_service.entity.UserApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserApprovalRepository extends JpaRepository<UserApproval, Integer> {
    Optional<UserApproval> findByUserId(String userId);
    Optional<UserApproval> findByUsername(String username);
}
