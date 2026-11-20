package com.mgrigorakis.cultivo.seedbeds.repository;

import com.mgrigorakis.cultivo.seedbeds.model.Seedbed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeedbedRepository extends JpaRepository<Seedbed, Long> {
    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
