package com.mgrigorakis.cultivo.plants;

import com.mgrigorakis.cultivo.plants.model.Plant;
import com.mgrigorakis.cultivo.plants.model.enums.PlantStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlantTest {
    @Test
    void newPlant_shouldHaveActiveStatusByDefault() {
        // Arrange & Act
        Plant plant = Plant.builder().label("Basil").build();

        // Assert
        assertEquals(PlantStatus.ACTIVE, plant.getStatus());
    }

    @Test
    void activeStatusPlant_shouldAllowTransitionToDead() {
        // Arrange
        Plant plant = Plant.builder().label("Basil").build();

        // Act
        boolean response = plant.canTransitionTo(PlantStatus.DEAD);

        // Assert
        assertTrue(response);
    }

    @Test
    void deadStatusPlant_shouldNotAllowTransitionToActive() {
        // Arrange
        Plant plant = Plant.builder().label("Basil").build();
        plant.setStatus(PlantStatus.DEAD);

        // Act
        boolean response = plant.canTransitionTo(PlantStatus.ACTIVE);

        // Assert
        assertFalse(response);
    }
}
