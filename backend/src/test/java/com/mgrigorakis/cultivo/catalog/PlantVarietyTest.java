package com.mgrigorakis.cultivo.catalog;

import com.mgrigorakis.cultivo.catalog.model.PlantSpecies;
import com.mgrigorakis.cultivo.catalog.model.PlantVariety;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlantVarietyTest {
    private PlantSpecies tomatoSpecies;

    @BeforeEach
    public void setUp() {
        tomatoSpecies = PlantSpecies.builder().name("Tomato").build();
        tomatoSpecies.setId(1L);
    }

    @Test
    void belongsTo_shouldReturnTrue_whenSpeciesMatch() {
        // Arrange
        PlantVariety tomatoVariety = PlantVariety.builder().name("Cherry").plantSpecies(tomatoSpecies).build();

        // Act & Assert
        assertTrue(tomatoVariety.belongsTo(tomatoSpecies));
    }

    @Test
    void belongsTo_shouldReturnFalse_whenSpeciesNotMatch() {
        // Arrange
        PlantSpecies basilSpecies = PlantSpecies.builder().name("Basil").build();
        basilSpecies.setId(2L);

        PlantVariety variety = PlantVariety.builder().name("Cherry").plantSpecies(tomatoSpecies).build();

        // Act & Assert
        assertFalse(variety.belongsTo(basilSpecies));
    }
}
