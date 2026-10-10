package com.mgrigorakis.cultivo.plants;

import com.mgrigorakis.cultivo.common.exceptions.ConflictException;
import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.plants.dto.PlantRequest;
import com.mgrigorakis.cultivo.plants.dto.PlantResponse;
import com.mgrigorakis.cultivo.plants.dto.PlantStatusRequest;
import com.mgrigorakis.cultivo.plants.mapper.PlantMapper;
import com.mgrigorakis.cultivo.plants.model.Plant;
import com.mgrigorakis.cultivo.plants.model.enums.PlantStatus;
import com.mgrigorakis.cultivo.plants.repository.PlantRepository;
import com.mgrigorakis.cultivo.plants.service.PlantServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlantServiceTest {
    @InjectMocks
    private PlantServiceImpl plantService;

    @Mock
    private PlantRepository plantRepository;

    @Mock
    private PlantMapper plantMapper;

    @Test
    void getPlantById_shouldReturnPlant_ifExists() {
        // Arrange
        Plant mockPlant = Plant.builder().label("Basil").build();
        when(plantRepository.findById(1L)).thenReturn(Optional.of(mockPlant));

        // Act
        plantService.getPlantById(1L);

        // Assert
        verify(plantRepository).findById(1L);
        verify(plantMapper).toResponse(mockPlant);
    }

    @Test
    void getPlantById_shouldThrowResourceNotFoundException_whenPlantDoesNotExist() {
        // Arrange
        when(plantRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> plantService.getPlantById(1L));

        verify(plantRepository).findById(1L);
        verify(plantMapper, never()).toResponse(any(Plant.class));
    }

    @Test
    void createPlant_shouldCreatePlant() {
        // Arrange
        PlantRequest mockRequest = new PlantRequest("Basil");
        Plant mockPlant = Plant.builder().label("Basil").build();
        PlantResponse mockResponse = new PlantResponse(1L, "Basil", PlantStatus.ACTIVE, null, null, null);

        when(plantMapper.toEntity(mockRequest)).thenReturn(mockPlant);
        when(plantRepository.save(mockPlant)).thenReturn(mockPlant);
        when(plantMapper.toResponse(mockPlant)).thenReturn(mockResponse);

        // Act
        PlantResponse result = plantService.createPlant(mockRequest);

        // Assert
        assertEquals(PlantStatus.ACTIVE, result.status());
        verify(plantMapper).toEntity(mockRequest);
        verify(plantRepository).save(mockPlant);
        verify(plantMapper).toResponse(mockPlant);
    }

    @Test
    void updatePlantById_shouldUpdatePlant_ifExists() {
        // Arrange
        PlantRequest mockRequest = new PlantRequest("Rose");
        Plant mockPlant = Plant.builder().label("Basil").build();
        Plant mockUpdatedPlant = Plant.builder().label("Rose").build();
        PlantResponse mockResponse = new PlantResponse(1L, "Rose", PlantStatus.ACTIVE, null, null, null);

        when(plantRepository.findById(1L)).thenReturn(Optional.of(mockPlant));
        when(plantMapper.toUpdate(mockPlant, mockRequest)).thenReturn(mockUpdatedPlant);
        when(plantRepository.save(mockUpdatedPlant)).thenReturn(mockUpdatedPlant);
        when(plantMapper.toResponse(mockUpdatedPlant)).thenReturn(mockResponse);

        // Act
        PlantResponse result = plantService.updatePlantById(1L, mockRequest);

        // Assert
        assertEquals("Rose", result.label());
        assertEquals(PlantStatus.ACTIVE, result.status());

        verify(plantRepository).findById(1L);
        verify(plantMapper).toUpdate(mockPlant, mockRequest);
        verify(plantRepository).save(mockUpdatedPlant);
        verify(plantMapper).toResponse(mockUpdatedPlant);
    }

    @Test
    void updatePlantById_shouldThrowResourceNotFoundException_whenPlantDoesNotExist() {
        // Arrange
        PlantRequest mockRequest = new PlantRequest("Basil");
        when(plantRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                plantService.updatePlantById(1L, mockRequest));

        verify(plantRepository).findById(1L);
        verify(plantRepository, never()).save(any(Plant.class));
        verify(plantMapper, never()).toResponse(any(Plant.class));
    }

    @Test
    void updatePlantStatusById_shouldUpdatePlantStatus_ifPlantExists() {
        // Arrange
        Plant mockPlant = Plant.builder().label("Basil").build();
        PlantStatusRequest mockRequest = new PlantStatusRequest(PlantStatus.DEAD);

        when(plantRepository.findById(1L)).thenReturn(Optional.of(mockPlant));
        when(plantRepository.save(mockPlant)).thenReturn(mockPlant);

        // Act
        plantService.updatePlantStatusById(1L, mockRequest);

        // Assert
        assertEquals(PlantStatus.DEAD, mockPlant.getStatus());

        verify(plantRepository).findById(1L);
        verify(plantRepository).save(mockPlant);
    }

    @Test
    void updatePlantStatusById_shouldThrowResourceNotFoundException_whenPlantDoesNotExist() {
        // Arrange
        PlantStatusRequest mockRequest = new PlantStatusRequest(PlantStatus.DEAD);
        when(plantRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> plantService.updatePlantStatusById(1L, mockRequest));

        verify(plantRepository).findById(1L);
        verify(plantRepository, never()).save(any(Plant.class));
    }

    @Test
    void updatePlantStatusById_shouldThrowConflictException_whenDesiredStatusViolatesTransition() {
        // Arrange
        Plant mockPlant = Plant.builder().label("Basil").build();
        mockPlant.setStatus(PlantStatus.DEAD);
        PlantStatusRequest mockRequest = new PlantStatusRequest(PlantStatus.ACTIVE);

        when(plantRepository.findById(1L)).thenReturn(Optional.of(mockPlant));

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class, () ->
                plantService.updatePlantStatusById(1L, mockRequest));

        assertEquals("STATUS_VIOLATION", exception.getErrorCode());

        verify(plantRepository).findById(1L);
        verify(plantRepository, never()).save(mockPlant);
    }
}
