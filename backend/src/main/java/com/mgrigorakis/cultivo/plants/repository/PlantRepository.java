package com.mgrigorakis.cultivo.plants.repository;

import com.mgrigorakis.cultivo.plants.model.Plant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PlantRepository extends JpaRepository<Plant, Long> {
    Page<Plant> findAllByArchivedAtIsNull(Pageable pageable);

    Page<Plant> findAllByArchivedAtIsNotNull(Pageable pageable);
}
