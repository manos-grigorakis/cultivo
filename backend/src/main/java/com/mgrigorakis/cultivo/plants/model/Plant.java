package com.mgrigorakis.cultivo.plants.model;

import com.mgrigorakis.cultivo.plants.model.enums.PlantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "plants")
@Entity
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plant_seq")
    @SequenceGenerator(name = "plant_seq", sequenceName = "plant_sequence", allocationSize = 5)
    private Long id;

    @Column(name = "label", nullable = false, length = 150)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PlantStatus status;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Plant(String label, PlantStatus status) {
        this.label = label;
        this.status = PlantStatus.ACTIVE;
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Check wether tha plant can transition to the specified status
     *
     * <p>Allowed Transition:</p>
     * <li>ACTIVE -> DEAD</li>
     * <li>Any status -> same status</li>
     * @param status The target status
     * @return {@code true} if the transition is allowed, otherwise {@code false}
     */
    public boolean canTransitionTo(PlantStatus status) {
        return this.status == status || (this.status == PlantStatus.ACTIVE && status == PlantStatus.DEAD);
    }
}
