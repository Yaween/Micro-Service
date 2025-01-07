package com.example.DistributorService.Repository;


import com.example.DistributorService.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    boolean existsByProductName(String productName);
}
