package com.mgrigorakis.cultivo.seedbeds.repository;

import com.mgrigorakis.cultivo.seedbeds.model.SeedbedSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeedbedSlotRepository extends JpaRepository<SeedbedSlot, Long> {
    List<SeedbedSlot> findAllBySeedbed_Id(Long seedbedId);

    Optional<SeedbedSlot> findByIdAndSeedbed_Id(Long id, Long seedbedId);

    void deleteAllBySeedbed_Id(Long seedbedId);
}
