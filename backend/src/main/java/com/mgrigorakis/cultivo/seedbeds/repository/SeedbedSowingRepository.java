package com.mgrigorakis.cultivo.seedbeds.repository;

import com.mgrigorakis.cultivo.seedbeds.model.SeedbedSowing;
import com.mgrigorakis.cultivo.seedbeds.model.enums.SeedbedSowingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeedbedSowingRepository extends JpaRepository<SeedbedSowing, Long>, JpaSpecificationExecutor<SeedbedSowing> {
    @Override
    @EntityGraph(attributePaths = {"slot", "packet", "slot.seedbed"})
    Page<SeedbedSowing> findAll(Specification<SeedbedSowing> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"slot", "packet", "slot.seedbed"})
    Optional<SeedbedSowing> findById(Long id);

    Optional<SeedbedSowing> findByIdAndSlot_IdAndSlot_Seedbed_Id(Long sowingId, Long slotId, Long seedbedId);

    boolean existsBySlot_IdAndStatusIn(Long slotId, List<SeedbedSowingStatus> statuses);

    boolean existsBySlot_Seedbed_id(Long seedbedId);
}
