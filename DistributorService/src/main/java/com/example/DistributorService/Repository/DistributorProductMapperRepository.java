package com.example.DistributorService.Repository;

import com.example.DistributorService.Entity.DistributorProductMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributorProductMapperRepository extends JpaRepository<DistributorProductMapper, String> {

}
