package com.mgrigorakis.cultivo.seedbeds.model;

import com.mgrigorakis.cultivo.seedbeds.model.enums.SeedbedSowingStatus;
import com.mgrigorakis.cultivo.seeds.model.SeedPacket;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = {"packet", "slot"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seedbed_sowings")
@Entity
public class SeedbedSowing {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seedbed_sowings_seq")
    @SequenceGenerator(name = "seedbed_sowings_seq", sequenceName = "seedbed_sowings_sequence", allocationSize = 10)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SeedbedSowingStatus status;

    @Column(name = "seed_count", nullable = false)
    private Integer seedCount;

    @Column(name = "germinated_count")
    private Integer germinatedCount;

    @Column(name = "transplanted_count")
    private Integer transplantedCount;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "sown_at", nullable = false)
    private LocalDateTime sownAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seedbed_slot_id", nullable = false)
    private SeedbedSlot slot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seed_packet_id", nullable = false)
    private SeedPacket packet;

    @Builder
    public SeedbedSowing(Integer seedCount, Integer germinatedCount, Integer transplantedCount, String notes,
                         LocalDateTime sownAt, LocalDateTime completedAt, SeedbedSlot slot, SeedPacket packet) {
        this.status = SeedbedSowingStatus.SOWN;
        this.seedCount = seedCount;
        this.germinatedCount = germinatedCount;
        this.transplantedCount = transplantedCount;
        this.notes = notes;
        this.sownAt = sownAt;
        this.completedAt = completedAt;
        this.slot = slot;
        this.packet = packet;
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
     * Validates if the status of the seedbed sowing is terminal
     *
     * <p>Terminal Statuses: FAILED or TRANSPLANTED</p>
     * @return {@code true} if the seedbed sowing status is terminal, otherwise {@code false}
     */
    public boolean isTerminalStatus() {
        return status.equals(SeedbedSowingStatus.FAILED) || status.equals(SeedbedSowingStatus.TRANSPLANTED);
    }

    /**
     * Checks whether the sowing can transition from its current status to the target status
     *
     * <p>Allowed status transitions</p>
     * <ul>
     *     <li>SOWN -> GERMINATING or FAILED</li>
     *     <li>GERMINATING -> GERMINATED or FAILED</li>
     *     <li>GERMINATED -> TRANSPLANTED or FAILED</li>
     * </ul>
     *
     * <p>FAILED and TRANSPLANTED are terminal statuses and do not allow transitions</p>
     *
     * @param targetStatus The target status of the seedbed sowing to validate
     * @return {@code true} if the transition is allowed, otherwise {@code false}
     */
    public boolean canTransitionTo(SeedbedSowingStatus targetStatus) {
        if (targetStatus == null || isTerminalStatus()) return false;

        return switch (status) {
            case SOWN -> targetStatus == SeedbedSowingStatus.GERMINATING || targetStatus == SeedbedSowingStatus.FAILED;
            case GERMINATING ->
                    targetStatus == SeedbedSowingStatus.GERMINATED || targetStatus == SeedbedSowingStatus.FAILED;
            case GERMINATED ->
                    targetStatus == SeedbedSowingStatus.TRANSPLANTED || targetStatus == SeedbedSowingStatus.FAILED;
            case FAILED, TRANSPLANTED -> false;
        };
    }
}
