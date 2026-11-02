package com.mgrigorakis.cultivo.plants.repository;

import com.mgrigorakis.cultivo.plants.model.Plant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PlantRepository extends JpaRepository<Plant, Long>, JpaSpecificationExecutor<Plant> {
    @Override
    @EntityGraph(attributePaths = {"species", "variety", "species.plantCategory"})
    Page<Plant> findAll(Specification<Plant> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"species", "variety", "species.plantCategory"})
    @Query("SELECT p FROM Plant AS p WHERE p.id = :id")
    Optional<Plant> findByIdWithSpeciesAndVariety(@Param("id") Long id);

    boolean existsBySpecies_Id(Long speciesId);

    boolean existsByVariety_Id(Long varietyId);
}
