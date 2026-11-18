package com.mgrigorakis.cultivo.seedbeds.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = {"seedbed"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seedbed_slots", uniqueConstraints = {
        @UniqueConstraint(name = "uk_seedbed_slots_position", columnNames = {"seedbed_id", "row_index", "column_index"})
})
@Entity
public class SeedbedSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seedbed_slots_seq")
    @SequenceGenerator(name = "seedbed_slots_seq", sequenceName = "seedbed_slots_sequence", allocationSize = 10)
    private Long id;

    @Column(name = "row_index", nullable = false)
    private Integer rowIndex;

    @Column(name = "column_index", nullable = false)
    private Integer columnIndex;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seedbed_id", nullable = false)
    private Seedbed seedbed;

    @Builder
    public SeedbedSlot(Integer rowIndex, Integer columnIndex, Seedbed seedbed) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.seedbed = seedbed;
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
