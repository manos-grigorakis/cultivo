package com.mgrigorakis.cultivo.catalog;

import com.mgrigorakis.cultivo.catalog.dto.PlantVarietyRequest;
import com.mgrigorakis.cultivo.catalog.dto.PlantVarietyResponse;
import com.mgrigorakis.cultivo.catalog.mapper.PlantVarietyMapper;
import com.mgrigorakis.cultivo.catalog.model.PlantSpecies;
import com.mgrigorakis.cultivo.catalog.model.PlantVariety;
import com.mgrigorakis.cultivo.catalog.repository.PlantSpeciesRepository;
import com.mgrigorakis.cultivo.catalog.repository.PlantVarietyRepository;
import com.mgrigorakis.cultivo.catalog.service.PlantVarietyServiceImpl;
import com.mgrigorakis.cultivo.common.exceptions.ConflictException;
import com.mgrigorakis.cultivo.common.exceptions.DuplicateEntryException;
import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.plants.repository.PlantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlantVarietyServiceTest {
    private PlantSpecies mockSpecies;
    private PlantVariety mockVariety;
    private PlantVarietyRequest mockVarietyCreateRequest;
    private PlantVarietyRequest mockVarietyUpdateRequest;
    private PlantVarietyResponse mockVarietyCreateResponse;
    private PlantVarietyResponse mockVarietyUpdateResponse;

    @InjectMocks
    private PlantVarietyServiceImpl plantVarietyService;

    @Mock
    private PlantVarietyRepository plantVarietyRepository;

    @Mock
    private PlantSpeciesRepository plantSpeciesRepository;

    @Mock
    private PlantRepository plantRepository;

    @Mock
    private PlantVarietyMapper plantVarietyMapper;


    @BeforeEach
    public void setUp() {
        mockSpecies = PlantSpecies.builder().name("Basil").build();
        mockSpecies.setId(1L);

        mockVariety = PlantVariety.builder().name("Sweet").plantSpecies(mockSpecies).build();
        mockVarietyCreateRequest = new PlantVarietyRequest("Sweet", null, 1L);
        mockVarietyCreateResponse = new PlantVarietyResponse(1L, "Sweet", null, null, null, 1L);

        mockVarietyUpdateRequest = new PlantVarietyRequest("Genovese", null, 1L);
        mockVarietyUpdateResponse = new PlantVarietyResponse(1L, "Genovese", null, null, null, 1L);
    }

    @Test
    void getPlantVarietyById_shouldReturnVariety_whenExist() {
        // Arrange
        when(plantVarietyRepository.findById(1L)).thenReturn(Optional.of(mockVariety));
        when(plantVarietyMapper.toResponse(mockVariety)).thenReturn(mockVarietyCreateResponse);

        // Act
        PlantVarietyResponse result = plantVarietyService.getPlantVarietyById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(mockVariety.getName(), result.name());

        verify(plantVarietyRepository).findById(1L);
        verify(plantVarietyMapper).toResponse(mockVariety);
    }

    @Test
    void getPlantVarietyById_shouldThrowResourceNotFoundException_whenNotExist() {
        // Arrange
        when(plantVarietyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> plantVarietyService.getPlantVarietyById(1L));

        verify(plantVarietyRepository).findById(1L);
        verify(plantVarietyMapper, never()).toResponse(mockVariety);
    }

    @Test
    void createPlantVariety_shouldCreatePlantVariety() {
        // Arrange
        when(plantVarietyRepository.existsByNameIgnoreCaseAndPlantSpecies_Id(
                mockVarietyCreateRequest.name(), mockVarietyCreateRequest.plantSpeciesId())).thenReturn(false);
        when(plantSpeciesRepository.findById(1L)).thenReturn(Optional.of(mockSpecies));
        when(plantVarietyMapper.toEntity(mockVarietyCreateRequest)).thenReturn(mockVariety);
        when(plantVarietyRepository.save(mockVariety)).thenReturn(mockVariety);
        when(plantVarietyMapper.toResponse(mockVariety)).thenReturn(mockVarietyCreateResponse);

        // Act
        PlantVarietyResponse result = plantVarietyService.createPlantVariety(mockVarietyCreateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(mockVarietyCreateResponse, result);
        assertEquals(mockVariety.getName(), result.name());

        verify(plantVarietyRepository).existsByNameIgnoreCaseAndPlantSpecies_Id(
                mockVarietyCreateRequest.name(), mockVarietyCreateRequest.plantSpeciesId());
        verify(plantSpeciesRepository).findById(1L);
        verify(plantVarietyMapper).toEntity(mockVarietyCreateRequest);
        verify(plantVarietyRepository).save(mockVariety);
        verify(plantVarietyMapper).toResponse(mockVariety);
    }

    @Test
    void createPlantVariety_shouldThrowDuplicateEntryException_whenVarietyExistsByName() {
        // Arrange
        when(plantVarietyRepository.existsByNameIgnoreCaseAndPlantSpecies_Id(
                mockVarietyCreateRequest.name(), mockVarietyCreateRequest.plantSpeciesId())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEntryException.class, () ->
                plantVarietyService.createPlantVariety(mockVarietyCreateRequest));

        verify(plantVarietyRepository).existsByNameIgnoreCaseAndPlantSpecies_Id(
                mockVarietyCreateRequest.name(), mockVarietyCreateRequest.plantSpeciesId());
        verifyNoMoreInteractions(plantVarietyRepository, plantSpeciesRepository, plantVarietyMapper);
    }

    @Test
    void createPlantVariety_shouldThrowResourceNotFoundException_whenSpeciesNotExist() {
        // Arrange
        when(plantVarietyRepository.existsByNameIgnoreCaseAndPlantSpecies_Id(
                mockVarietyCreateRequest.name(), mockVarietyCreateRequest.plantSpeciesId())).thenReturn(false);
        when(plantSpeciesRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                plantVarietyService.createPlantVariety(mockVarietyCreateRequest));

        verify(plantVarietyRepository).existsByNameIgnoreCaseAndPlantSpecies_Id(
                mockVarietyCreateRequest.name(), mockVarietyCreateRequest.plantSpeciesId());
        verify(plantSpeciesRepository).findById(1L);
        verifyNoMoreInteractions(plantVarietyRepository, plantSpeciesRepository, plantVarietyMapper);
    }

    @Test
    void updatePlantVarietyById_shouldUpdatePlantVariety_whenExist() {
        // Arrange
        PlantVariety mockUpdatedVariety = PlantVariety.builder().name("Genovese").build();

        when(plantVarietyRepository.findById(1L)).thenReturn(Optional.of(mockVariety));
        when(plantVarietyRepository.existsByNameIgnoreCaseAndIdNotAndPlantSpecies_Id(
                mockVarietyUpdateRequest.name(), 1L, mockVarietyUpdateRequest.plantSpeciesId()
        )).thenReturn(false);
        when(plantSpeciesRepository.findById(1L)).thenReturn(Optional.of(mockSpecies));
        when(plantVarietyMapper.toUpdate(mockVariety, mockVarietyUpdateRequest)).thenReturn(mockUpdatedVariety);
        when(plantVarietyRepository.save(mockUpdatedVariety)).thenReturn(mockUpdatedVariety);
        when(plantVarietyMapper.toResponse(mockUpdatedVariety)).thenReturn(mockVarietyUpdateResponse);

        // Act
        PlantVarietyResponse result = plantVarietyService.updatePlantVarietyById(1L, mockVarietyUpdateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(mockVarietyUpdateResponse, result);
        assertEquals(mockUpdatedVariety.getName(), result.name());

        verify(plantVarietyRepository).findById(1L);
        verify(plantVarietyRepository).existsByNameIgnoreCaseAndIdNotAndPlantSpecies_Id(
                mockVarietyUpdateRequest.name(), 1L, mockVarietyUpdateRequest.plantSpeciesId());
        verify(plantSpeciesRepository).findById(1L);
        verify(plantVarietyMapper).toUpdate(mockVariety, mockVarietyUpdateRequest);
        verify(plantVarietyRepository).save(mockUpdatedVariety);
        verify(plantVarietyMapper).toResponse(mockUpdatedVariety);
    }

    @Test
    void updatePlantVarietyById_shouldThrowResourceNotFoundException_whenVarietyNotExit() {
        // Arrange
        when(plantVarietyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                plantVarietyService.updatePlantVarietyById(1L, mockVarietyUpdateRequest));

        verify(plantVarietyRepository).findById(1L);
        verifyNoMoreInteractions(plantVarietyRepository, plantSpeciesRepository, plantVarietyMapper);
    }

    @Test
    void updatePlantVarietyById_shouldThrowDuplicateEntryException_whenVarietyExistsByName() {
        // Arrange
        when(plantVarietyRepository.findById(1L)).thenReturn(Optional.of(mockVariety));
        when(plantVarietyRepository.existsByNameIgnoreCaseAndIdNotAndPlantSpecies_Id(
                mockVarietyUpdateRequest.name(), 1L, mockVarietyUpdateRequest.plantSpeciesId()
        )).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEntryException.class, () ->
                plantVarietyService.updatePlantVarietyById(1L, mockVarietyUpdateRequest));

        verify(plantVarietyRepository).findById(1L);
        verify(plantVarietyRepository).existsByNameIgnoreCaseAndIdNotAndPlantSpecies_Id(
                mockVarietyUpdateRequest.name(), 1L, mockVarietyUpdateRequest.plantSpeciesId());
        verifyNoMoreInteractions(plantVarietyRepository, plantSpeciesRepository, plantVarietyMapper);
    }

    @Test
    void deletePlantVarietyById_shouldDeletePlantVariety() {
        // Arrange
        when(plantVarietyRepository.findById(1L)).thenReturn(Optional.of(mockVariety));
        when(plantRepository.existsByVariety_Id(1L)).thenReturn(false);

        // Act
        plantVarietyService.deletePlantVarietyById(1L);

        // Assert
        verify(plantVarietyRepository).findById(1L);
        verify(plantRepository).existsByVariety_Id(1L);
        verify(plantVarietyRepository).delete(mockVariety);
    }

    @Test
    void deletePlantVarietyById_shouldThrowResourceNotFoundException_whenNotExist() {
        // Arrange
        when(plantVarietyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> plantVarietyService.deletePlantVarietyById(1L));

        // Assert
        verify(plantVarietyRepository).findById(1L);
        verifyNoMoreInteractions(plantVarietyRepository);
    }

    @Test
    void deletePlantVarietyById_shouldThrowConflictException_whenVarietyHasPlants() {
        // Arrange
        when(plantVarietyRepository.findById(1L)).thenReturn(Optional.of(mockVariety));
        when(plantRepository.existsByVariety_Id(1L)).thenReturn(true);

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class, () ->
                plantVarietyService.deletePlantVarietyById(1L));

        // Assert
        assertEquals("PLANT_VARIETY_HAS_PLANTS", exception.getErrorCode());

        verify(plantVarietyRepository).findById(1L);
        verify(plantRepository).existsByVariety_Id(1L);
        verifyNoMoreInteractions(plantVarietyRepository);
    }
}
