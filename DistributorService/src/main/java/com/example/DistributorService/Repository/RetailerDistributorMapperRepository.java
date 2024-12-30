package com.example.DistributorService.Repository;

import com.example.DistributorService.Entity.RetailerDistributorMapper;
import com.example.DistributorService.Entity.RetailerRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RetailerDistributorMapperRepository extends JpaRepository<RetailerDistributorMapper, String> {

}

