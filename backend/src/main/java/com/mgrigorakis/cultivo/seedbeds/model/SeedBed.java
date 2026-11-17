package com.mgrigorakis.cultivo.seedbeds.model;

import com.mgrigorakis.cultivo.seedbeds.model.enums.SeedBedLocationType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seedbeds")
@Entity
public class SeedBed {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seedbeds_seq")
    @SequenceGenerator(name = "seedbeds_seq", sequenceName = "seedbeds_sequence", allocationSize = 10)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false)
    private SeedBedLocationType locationType;

    @Column(name = "rows_count", nullable = false)
    private Integer rowsCount;

    @Column(name = "columns_count", nullable = false)
    private Integer columnsCount;

    @Column(name = "length_cm", precision = 6, scale = 2)
    private BigDecimal lengthCm;

    @Column(name = "width_cm", precision = 6, scale = 2)
    private BigDecimal widthCm;

    @Column(name = "soil_depth_cm", precision = 5, scale = 2)
    private BigDecimal soilDepthCm;

    @Column(name = "has_enclosure", nullable = false)
    private Boolean hasEnclosure;

    @Column(name = "has_humidity_control", nullable = false)
    private Boolean hasHumidityControl;

    @Column(name = "has_automatic_watering", nullable = false)
    private Boolean hasAutomaticWatering;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public SeedBed(String name, SeedBedLocationType locationType, Integer rowsCount, Integer columnsCount, BigDecimal lengthCm,
                   BigDecimal widthCm, BigDecimal soilDepthCm, Boolean hasEnclosure, Boolean hasHumidityControl,
                   Boolean hasAutomaticWatering, String notes) {
        this.name = name;
        this.locationType = locationType;
        this.rowsCount = rowsCount;
        this.columnsCount = columnsCount;
        this.lengthCm = lengthCm;
        this.widthCm = widthCm;
        this.soilDepthCm = soilDepthCm;
        this.hasEnclosure = hasEnclosure != null && hasEnclosure;
        this.hasHumidityControl = hasHumidityControl != null && hasHumidityControl;
        this.hasAutomaticWatering = hasAutomaticWatering != null && hasAutomaticWatering;
        this.notes = notes;
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
