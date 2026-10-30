package com.mgrigorakis.cultivo.plants.repository;

import com.mgrigorakis.cultivo.plants.model.PlantCategory;
import com.mgrigorakis.cultivo.plants.model.PlantSpecies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlantSpeciesRepository extends JpaRepository<PlantSpecies, Long> {
    @EntityGraph(attributePaths = "plantCategory")
    Page<PlantSpecies> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "plantCategory")
    Optional<PlantSpecies> findWithPlantCategoryById(Long id);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByScientificNameIgnoreCase(String scientificName);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByScientificNameIgnoreCaseAndIdNot(String scientificName, Long id);

    boolean existsByPlantCategoryId(Long plantCategoryId);
}
