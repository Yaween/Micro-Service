package com.example.DistributorService.Repository;

import com.example.DistributorService.Entity.RetailerRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RetailerRequestRepository extends JpaRepository<RetailerRequest, String> {
    Optional<RetailerRequest>  findByRetailerRequestId(String retailerRequestId);
    List<RetailerRequest> findByDistributorId(String distributorId);

    List<RetailerRequest> findByDistributorIdAndStatus(String distributorId, String status);
}
