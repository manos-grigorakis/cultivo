package com.mgrigorakis.cultivo.catalog.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = {"plantSpecies"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "plant_varieties",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_plant_varieties_species_name",
                        columnNames = {"plant_species_id", "name"}
                )
        }
)
@Entity
public class PlantVariety {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plant_varieties_seq")
    @SequenceGenerator(name = "plant_varieties_seq", sequenceName = "plant_varieties_sequence", allocationSize = 20)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_species_id", nullable = false)
    private PlantSpecies plantSpecies;

    @Builder
    public PlantVariety(String name, String description, PlantSpecies plantSpecies) {
        this.name = name;
        this.description = description;
        this.plantSpecies = plantSpecies;
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
