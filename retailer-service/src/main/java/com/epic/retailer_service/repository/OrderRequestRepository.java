package com.epic.retailer_service.repository;

import com.epic.retailer_service.entity.OrderRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRequestRepository extends JpaRepository<OrderRequest, String> {
//    Optional<OrderRequest> findAllByRetailerId(String retailerId);

    List<OrderRequest> findAll(String retailerId);
}
