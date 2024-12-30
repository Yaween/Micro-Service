package com.example.DistributorService.Repository;


import com.example.DistributorService.Entity.DistributorProductMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistributorProductMapperRepository extends JpaRepository<DistributorProductMapper, Long> {

    Optional<DistributorProductMapper> findByDistributorIdAndProductId(Long productId, Long distributorId);
    List<DistributorProductMapper> findByDistributorId(Long distributorId);

}
