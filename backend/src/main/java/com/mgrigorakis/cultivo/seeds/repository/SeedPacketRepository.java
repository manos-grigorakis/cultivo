package com.mgrigorakis.cultivo.seeds.repository;

import com.mgrigorakis.cultivo.seeds.model.SeedPacket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeedPacketRepository extends JpaRepository<SeedPacket, Long> {
    @Override
    @EntityGraph(attributePaths = {"plantVariety"})
    Page<SeedPacket> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"plantVariety"})
    Optional<SeedPacket> findById(Long id);

    boolean existsByPlantVariety_IdAndBrandAndCode(Long plantVarietyId, String brand, String code);

    boolean existsByPlantVariety_IdAndBrandAndCodeAndIdNot(Long plantVarietyId, String brand, String code, Long id);
}
