package com.epic.retailer_service.repository;

import com.epic.retailer_service.entity.AddDistributorReq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddDistributorReqRepository extends JpaRepository<AddDistributorReq, String> {
//    Optional<AddDistributorReq> findByRetailerId(String retailerId);
    List<AddDistributorReq> findByRetailerId(String retailerId);
    Optional<AddDistributorReq> findByRetailerIdAndDistributorId(String retailerId, String distributorId);
}
