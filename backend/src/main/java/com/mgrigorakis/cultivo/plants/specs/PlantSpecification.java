package com.mgrigorakis.cultivo.plants.specs;

import com.mgrigorakis.cultivo.plants.model.Plant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlantSpecification {
    public static Specification<Plant> isArchived(Boolean archived) {
        return (root, criteriaQuery, criteriaBuilder) -> Boolean.TRUE.equals(archived)
                ? criteriaBuilder.isNotNull(root.get("archivedAt"))
                : criteriaBuilder.isNull(root.get("archivedAt"));
    }

    public static Specification<Plant> hasSpecies(Long speciesId) {
        return (root, criteriaQuery, criteriaBuilder) -> speciesId == null
                ? null
                : criteriaBuilder.equal(root.get("species").get("id"), speciesId);
    }

    public static Specification<Plant> hasVariety(Long varietyId) {
        return (root, criteriaQuery, criteriaBuilder) -> varietyId == null
                ? null
                : criteriaBuilder.equal(root.get("variety").get("id"), varietyId);
    }
}
