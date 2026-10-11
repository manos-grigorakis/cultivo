package com.mgrigorakis.cultivo.plants.events.repository;

import com.mgrigorakis.cultivo.plants.events.model.PlantEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantEventRepository extends JpaRepository<PlantEvent, Long> {
    Page<PlantEvent> findAllByPlantId(Long plantId, Pageable pageable);
}
