package com.example.DistributorService.Repository;


import com.example.DistributorService.Entity.Distributor;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface DistributorRepository extends JpaRepository<Distributor, String> {
    Optional<Distributor> findByUserId(String userId);
}

