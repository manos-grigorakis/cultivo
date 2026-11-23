package com.mgrigorakis.cultivo.seedbeds.specs;

import com.mgrigorakis.cultivo.seedbeds.model.SeedbedSowing;
import com.mgrigorakis.cultivo.seedbeds.model.enums.SeedbedSowingStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SeedbedSowingSpecs {
    public static Specification<SeedbedSowing> hasPacketId(Long packetId) {
        return (root, criteriaQuery, criteriaBuilder) -> packetId == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("packet").get("id"), packetId);
    }

    public static Specification<SeedbedSowing> hasSlotId(Long slotId) {
        return (root, criteriaQuery, criteriaBuilder) -> slotId == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("slot").get("id"), slotId);
    }

    public static Specification<SeedbedSowing> hasSeedbedId(Long seedbedId) {
        return (root, criteriaQuery, criteriaBuilder) -> seedbedId == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("slot").get("seedbed").get("id"), seedbedId);
    }

    public static Specification<SeedbedSowing> hasStatus(SeedbedSowingStatus status) {
        return (root, criteriaQuery, criteriaBuilder) -> status == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("status"), status);
    }
}
