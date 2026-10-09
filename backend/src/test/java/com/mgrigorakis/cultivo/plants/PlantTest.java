package com.mgrigorakis.cultivo.plants;

import com.mgrigorakis.cultivo.plants.model.Plant;
import com.mgrigorakis.cultivo.plants.model.enums.PlantStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlantTest {
    @Test
    void newPlant_shouldHaveActiveStatusByDefault() {
        // Arrange & Act
        Plant plant = Plant.builder().label("Basil").build();

        // Assert
        assertEquals(PlantStatus.ACTIVE, plant.getStatus());
    }
}
