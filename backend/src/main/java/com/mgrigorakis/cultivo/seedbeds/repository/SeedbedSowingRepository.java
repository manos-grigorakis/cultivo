package com.mgrigorakis.cultivo.seedbeds.repository;

import com.mgrigorakis.cultivo.seedbeds.model.SeedbedSowing;
import com.mgrigorakis.cultivo.seedbeds.model.enums.SeedbedSowingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeedbedSowingRepository extends JpaRepository<SeedbedSowing, Long> {
    @Override
    @EntityGraph(attributePaths = {"slot", "packet"})
    Optional<SeedbedSowing> findById(Long id);

    Optional<SeedbedSowing> findByIdAndSlot_IdAndSlot_Seedbed_Id(Long sowingId, Long slotId, Long seedbedId);

    boolean existsBySlot_IdAndStatusIn(Long slotId, List<SeedbedSowingStatus> statuses);
}
