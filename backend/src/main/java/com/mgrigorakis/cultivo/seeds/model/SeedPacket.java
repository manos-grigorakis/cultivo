package com.mgrigorakis.cultivo.seeds.model;

import com.mgrigorakis.cultivo.catalog.model.PlantVariety;
import com.mgrigorakis.cultivo.seeds.enums.SeedPacketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"plantVariety"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "seed_packets",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_seed_packets_brand_code_plant_variety_id",
                        columnNames = {"brand", "code", "plant_variety_id"}
                )
        }
)
@Entity
public class SeedPacket {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seed_packets_seq")
    @SequenceGenerator(name = "seed_packets_seq", sequenceName = "seed_packets_sequence", allocationSize = 10)
    private Long id;

    @Column(name = "brand", nullable = false, length = 150)
    private String brand;

    @Column(name = "supplier", length = 150)
    private String supplier;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private SeedPacketStatus status;

    @Column(name = "code", length = 100)
    private String code;

    @Column(name = "height_cm", precision = 7, scale = 2)
    private BigDecimal heightCm;

    @Column(name = "weight_g", precision = 10, scale = 3)
    private BigDecimal weightG;

    @Column(name = "plant_spacing_cm", precision = 7, scale = 2)
    private BigDecimal plantSpacingCm;

    @Column(name = "row_spacing_cm", precision = 7, scale = 2)
    private BigDecimal rowSpacingCm;

    @Column(name = "sowing_depth_cm", precision = 5, scale = 2)
    private BigDecimal sowingDepthCm;

    @Column(name = "germination_days")
    private Integer germinationDays;

    @Column(name = "start_harvest_days")
    private Integer startHarvestDays;

    @Column(name = "sowing_outdoor_months", columnDefinition = "SMALLINT[]")
    private List<Short> sowingOutdoorMonths = new ArrayList<>();

    @Column(name = "sowing_seedbed_months", columnDefinition = "SMALLINT[]")
    private List<Short> sowingSeedbedMonths = new ArrayList<>();

    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_variety_id", nullable = false)
    private PlantVariety plantVariety;

    @Builder
    public SeedPacket(String brand, String supplier, String code, BigDecimal heightCm, BigDecimal weightG,
                      BigDecimal plantSpacingCm, BigDecimal rowSpacingCm, BigDecimal sowingDepthCm,
                      Integer germinationDays,
                      Integer startHarvestDays, List<Short> sowingOutdoorMonths, List<Short> sowingSeedbedMonths,
                      LocalDateTime purchasedAt, PlantVariety plantVariety) {
        this.brand = brand;
        this.supplier = supplier;
        this.status = SeedPacketStatus.STORED;
        this.code = code;
        this.heightCm = heightCm;
        this.weightG = weightG;
        this.plantSpacingCm = plantSpacingCm;
        this.rowSpacingCm = rowSpacingCm;
        this.sowingDepthCm = sowingDepthCm;
        this.germinationDays = germinationDays;
        this.startHarvestDays = startHarvestDays;
        this.sowingOutdoorMonths = sowingOutdoorMonths;
        this.sowingSeedbedMonths = sowingSeedbedMonths;
        this.purchasedAt = purchasedAt;
        this.plantVariety = plantVariety;
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
