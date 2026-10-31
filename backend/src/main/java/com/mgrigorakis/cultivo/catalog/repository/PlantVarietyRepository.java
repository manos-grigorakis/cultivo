package com.mgrigorakis.cultivo.catalog.repository;

import com.mgrigorakis.cultivo.catalog.model.PlantVariety;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantVarietyRepository extends JpaRepository<PlantVariety, Long> {
    Page<PlantVariety> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<PlantVariety> findByPlantSpecies_IdAndNameContainingIgnoreCase(Long plantSpeciesId, String name, Pageable pageable);

    boolean existsByNameIgnoreCaseAndPlantSpecies_Id(String name, Long plantSpeciesId);

    boolean existsByNameIgnoreCaseAndIdNotAndPlantSpecies_Id(String name, Long id, Long plantSpeciesId);

    boolean existsByPlantSpeciesId(Long plantSpeciesId);
}
