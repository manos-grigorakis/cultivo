package com.mgrigorakis.cultivo.plants.repository;

import com.mgrigorakis.cultivo.plants.model.PlantCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantCategoryRepository extends JpaRepository<PlantCategory, Long> {
    Page<PlantCategory> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
