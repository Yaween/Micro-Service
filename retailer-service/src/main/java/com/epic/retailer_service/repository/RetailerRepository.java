package com.epic.retailer_service.repository;

import com.epic.retailer_service.entity.Retailer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RetailerRepository extends JpaRepository<Retailer, String> {
    Optional<Retailer> findByUserId(String userId);
}
