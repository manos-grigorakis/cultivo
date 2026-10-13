package com.mgrigorakis.cultivo.plants.events.model;

import com.mgrigorakis.cultivo.plants.events.model.enums.PlantEventType;
import com.mgrigorakis.cultivo.plants.model.Plant;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "plant_events")
@Entity
public class PlantEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plant_event_seq")
    @SequenceGenerator(name = "plant_event_seq", sequenceName = "plant_event_sequence", allocationSize = 10)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private PlantEventType type;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @Builder
    public PlantEvent(String title, String description, PlantEventType type, LocalDateTime occurredAt, Plant plant) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.occurredAt = occurredAt != null ? occurredAt : LocalDateTime.now();
        this.plant = plant;
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
