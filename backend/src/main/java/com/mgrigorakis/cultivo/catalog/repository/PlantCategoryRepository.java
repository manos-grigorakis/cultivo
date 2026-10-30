package com.mgrigorakis.cultivo.catalog.repository;

import com.mgrigorakis.cultivo.catalog.model.PlantCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantCategoryRepository extends JpaRepository<PlantCategory, Long> {
    Page<PlantCategory> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
