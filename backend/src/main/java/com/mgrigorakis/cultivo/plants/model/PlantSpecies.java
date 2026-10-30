package com.mgrigorakis.cultivo.plants.model;

import com.mgrigorakis.cultivo.plants.model.enums.LifeCycle;
import com.mgrigorakis.cultivo.plants.model.enums.LightRequirement;
import com.mgrigorakis.cultivo.plants.model.enums.WaterRequirement;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "plant_species")
@Entity
public class PlantSpecies {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plant_species_seq")
    @SequenceGenerator(name = "plant_species_seq", sequenceName = "plant_species_seq_sequence", allocationSize = 5)
    private Long id;

    @Column(name = "name", unique = true, nullable = false, length = 150)
    private String name;

    @Column(name = "scientific_name", unique = true)
    private String scientificName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "life_cycle", length = 50)
    private LifeCycle lifeCycle;

    @Enumerated(EnumType.STRING)
    @Column(name = "light_requirement", length = 50)
    private LightRequirement lightRequirement;

    @Enumerated(EnumType.STRING)
    @Column(name = "water_requirement", length = 50)
    private WaterRequirement waterRequirement;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_category_id", nullable = false)
    private PlantCategory plantCategory;

    @Builder
    public PlantSpecies(String name, String scientificName, String description, LifeCycle lifeCycle,
                        LightRequirement lightRequirement, WaterRequirement waterRequirement,
                        PlantCategory plantCategory) {
        this.name = name;
        this.scientificName = scientificName;
        this.description = description;
        this.lifeCycle = lifeCycle;
        this.lightRequirement = lightRequirement;
        this.waterRequirement = waterRequirement;
        this.plantCategory = plantCategory;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
