package com.mgrigorakis.cultivo.plants;

import com.mgrigorakis.cultivo.catalog.model.PlantSpecies;
import com.mgrigorakis.cultivo.catalog.model.PlantVariety;
import com.mgrigorakis.cultivo.catalog.repository.PlantSpeciesRepository;
import com.mgrigorakis.cultivo.catalog.repository.PlantVarietyRepository;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlantServiceTest {
    private PlantSpecies mockSpecies;
    private PlantVariety mockVariety;
    private Plant mockPlant;
    private Plant mockUpdatedPlant;
    private PlantRequest mockPlantCreateRequest;
    private PlantRequest mockPlantUpdateRequest;
    private PlantResponse mockPlantCreateResponse;
    private PlantResponse mockPlantUpdateResponse;

    @InjectMocks
    private PlantServiceImpl plantService;

    @Mock
    private PlantRepository plantRepository;

    @Mock
    private PlantSpeciesRepository plantSpeciesRepository;

    @Mock
    private PlantVarietyRepository plantVarietyRepository;

    @Mock
    private PlantMapper plantMapper;

    @BeforeEach
    void setUp() {
        mockSpecies = PlantSpecies.builder().name("Basil").build();
        mockVariety = PlantVariety.builder().name("Genovese").plantSpecies(mockSpecies).build();

        mockSpecies.setId(1L);
        mockVariety.setId(1L);

        mockPlant = Plant.builder().label("Basil #1").species(mockSpecies).variety(mockVariety).build();
        mockUpdatedPlant = Plant.builder().label("Basil #2").species(mockSpecies).variety(mockVariety).build();

        mockPlantCreateRequest = new PlantRequest("Basil #1", 1L, 1L);
        mockPlantCreateResponse = new PlantResponse(1L, "Basil #1", PlantStatus.ACTIVE, null, null, null, null, null);

        mockPlantUpdateRequest = new PlantRequest("Basil #2", 1L, 1L);
        mockPlantUpdateResponse = new PlantResponse(1L, "Basil #2", PlantStatus.ACTIVE, null, null, null, null, null);
    }

    @Test
    void getPlantById_shouldReturnPlant_ifExists() {
        // Arrange
        when(plantRepository.findByIdWithSpeciesAndVariety(1L)).thenReturn(Optional.of(mockPlant));
        when(plantMapper.toResponse(mockPlant)).thenReturn(mockPlantCreateResponse);

        // Act
        PlantResponse result = plantService.getPlantById(1L);

        // Assert
        assertEquals(mockPlantCreateResponse, result);

        verify(plantRepository).findByIdWithSpeciesAndVariety(1L);
        verify(plantMapper).toResponse(mockPlant);
    }

    @Test
    void getPlantById_shouldThrowResourceNotFoundException_whenPlantDoesNotExist() {
        // Arrange
        when(plantRepository.findByIdWithSpeciesAndVariety(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> plantService.getPlantById(1L));

        verify(plantRepository).findByIdWithSpeciesAndVariety(1L);
        verify(plantMapper, never()).toResponse(any(Plant.class));
    }

    @Test
    void createPlant_shouldCreatePlant() {
        // Arrange
        when(plantSpeciesRepository.findById(1L)).thenReturn(Optional.of(mockSpecies));
        when(plantVarietyRepository.findById(1L)).thenReturn(Optional.of(mockVariety));
        when(plantMapper.toEntity(mockPlantCreateRequest, mockSpecies, mockVariety)).thenReturn(mockPlant);
        when(plantRepository.save(mockPlant)).thenReturn(mockPlant);
        when(plantMapper.toResponse(mockPlant)).thenReturn(mockPlantCreateResponse);

        // Act
        PlantResponse result = plantService.createPlant(mockPlantCreateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(mockPlantCreateResponse, result);
        assertEquals(mockPlantCreateResponse.label(), result.label());
        assertEquals(PlantStatus.ACTIVE, result.status());

        verify(plantSpeciesRepository).findById(1L);
        verify(plantVarietyRepository).findById(1L);
        verify(plantMapper).toEntity(mockPlantCreateRequest, mockSpecies, mockVariety);
        verify(plantRepository).save(mockPlant);
        verify(plantMapper).toResponse(mockPlant);
    }

    @Test
    void createPlant_shouldThrowResourceNotFoundException_whenSpeciesDoesNotExist() {
        // Arrange
        when(plantSpeciesRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> plantService.createPlant(mockPlantCreateRequest));

        verify(plantSpeciesRepository).findById(1L);
        verifyNoMoreInteractions(plantRepository,  plantSpeciesRepository,  plantVarietyRepository,  plantMapper);
    }

    @Test
    void createPlant_shouldCreatePlant_withNoVariety() {
        // Arrange
        Plant plantWithoutVariety = Plant.builder().label("Basil #1").species(mockSpecies).build();
        PlantRequest request = new PlantRequest("Basil #1", 1L, null);

        when(plantSpeciesRepository.findById(1L)).thenReturn(Optional.of(mockSpecies));
        when(plantMapper.toEntity(request, mockSpecies, null)).thenReturn(plantWithoutVariety);
        when(plantRepository.save(plantWithoutVariety)).thenReturn(plantWithoutVariety);
        when(plantMapper.toResponse(plantWithoutVariety)).thenReturn(mockPlantCreateResponse);

        // Act
        PlantResponse result = plantService.createPlant(request);

        // Assert
        assertNotNull(result);
        assertEquals(mockPlantCreateResponse, result);
        assertNull(result.variety());

        verify(plantSpeciesRepository).findById(1L);
        verifyNoMoreInteractions(plantVarietyRepository);
        verify(plantMapper).toEntity(request, mockSpecies, null);
        verify(plantRepository).save(plantWithoutVariety);
        verify(plantMapper).toResponse(plantWithoutVariety);
    }

    @Test
    void createPlant_shouldThrowConflictException_whenVarietyDoesNotBelongToSpecies() {
        // Arrange
        PlantSpecies species = PlantSpecies.builder().name("Tomato").build();
        species.setId(2L);

        PlantVariety variety = PlantVariety.builder().name("Cherry").plantSpecies(species).build();
        variety.setId(1L);

        when(plantSpeciesRepository.findById(1L)).thenReturn(Optional.of(mockSpecies));
        when(plantVarietyRepository.findById(1L)).thenReturn(Optional.of(variety));

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class, () ->
                plantService.createPlant(mockPlantCreateRequest));


        assertEquals("VARIETY_NOT_BELONGS_TO_SPECIES", exception.getErrorCode());

        verify(plantSpeciesRepository).findById(1L);
        verify(plantVarietyRepository).findById(1L);
        verifyNoMoreInteractions(plantVarietyRepository, plantSpeciesRepository, plantRepository, plantMapper);
    }

    @Test
    void updatePlantById_shouldUpdatePlant_ifExists() {
        // Arrange
        when(plantRepository.findById(1L)).thenReturn(Optional.of(mockPlant));
        when(plantSpeciesRepository.findById(1L)).thenReturn(Optional.of(mockSpecies));
        when(plantVarietyRepository.findById(1L)).thenReturn(Optional.of(mockVariety));
        when(plantMapper.toUpdate(mockPlant, mockPlantUpdateRequest, mockSpecies, mockVariety)).thenReturn(
                mockUpdatedPlant);
        when(plantRepository.save(mockUpdatedPlant)).thenReturn(mockUpdatedPlant);
        when(plantMapper.toResponse(mockUpdatedPlant)).thenReturn(mockPlantUpdateResponse);

        // Act
        PlantResponse result = plantService.updatePlantById(1L, mockPlantUpdateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(mockPlantUpdateResponse.label(), result.label());
        assertEquals(PlantStatus.ACTIVE, result.status());

        verify(plantRepository).findById(1L);
        verify(plantSpeciesRepository).findById(1L);
        verify(plantVarietyRepository).findById(1L);
        verify(plantMapper).toUpdate(mockPlant, mockPlantUpdateRequest, mockSpecies, mockVariety);
        verify(plantRepository).save(mockUpdatedPlant);
        verify(plantMapper).toResponse(mockUpdatedPlant);
    }

    @Test
    void updatePlantById_shouldThrowResourceNotFoundException_whenPlantDoesNotExist() {
        // Arrange
        when(plantRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> plantService.updatePlantById(1L, mockPlantUpdateRequest));

        verify(plantRepository).findById(1L);
        verifyNoMoreInteractions(plantRepository, plantMapper, plantSpeciesRepository, plantVarietyRepository);
    }

    @Test
    void updatePlantById_shouldThrowConflictException_whenPlantIsArchived() {
        // Arrange
        mockPlant.setArchivedAt(LocalDateTime.now());

        when(plantRepository.findById(1L)).thenReturn(Optional.of(mockPlant));

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class, () ->
                plantService.updatePlantById(1L, mockPlantUpdateRequest));

        assertEquals("PLANT_ARCHIVED", exception.getErrorCode());

        verify(plantRepository).findById(1L);
        verifyNoMoreInteractions(plantRepository, plantMapper, plantSpeciesRepository, plantVarietyRepository);
    }

    @Test
    void updatePlantById_shouldThrowResourceNotFoundException_whenVarietyDoesNotExist() {
        // Arrange
        when(plantRepository.findById(1L)).thenReturn(Optional.of(mockPlant));
        when(plantSpeciesRepository.findById(1L)).thenReturn(Optional.of(mockSpecies));
        when(plantVarietyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> plantService.updatePlantById(1L, mockPlantUpdateRequest));

        verify(plantRepository).findById(1L);
        verify(plantSpeciesRepository).findById(1L);
        verify(plantVarietyRepository).findById(1L);
        verifyNoMoreInteractions(plantRepository, plantSpeciesRepository, plantVarietyRepository, plantMapper);
    }

    @Test
    void updatePlantStatusById_shouldUpdatePlantStatus_ifPlantExists() {
        // Arrange
        when(plantRepository.findById(1L)).thenReturn(Optional.of(mockPlant));
        when(plantRepository.save(mockPlant)).thenReturn(mockPlant);

        // Act
        plantService.updatePlantStatusById(1L, new PlantStatusRequest(PlantStatus.DEAD));

        // Assert
        assertEquals(PlantStatus.DEAD, mockPlant.getStatus());

        verify(plantRepository).findById(1L);
        verify(plantRepository).save(mockPlant);
    }

    @Test
    void updatePlantStatusById_shouldThrowResourceNotFoundException_whenPlantDoesNotExist() {
        // Arrange
        when(plantRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                plantService.updatePlantStatusById(1L, new PlantStatusRequest(PlantStatus.DEAD)));

        verify(plantRepository).findById(1L);
        verify(plantRepository, never()).save(any(Plant.class));
    }

    @Test
    void updatePlantStatusById_shouldThrowConflictException_whenDesiredStatusViolatesTransition() {
        // Arrange
        mockPlant.setStatus(PlantStatus.DEAD);

        when(plantRepository.findById(1L)).thenReturn(Optional.of(mockPlant));

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class, () ->
                plantService.updatePlantStatusById(1L, new PlantStatusRequest(PlantStatus.ACTIVE)));

        assertEquals("STATUS_VIOLATION", exception.getErrorCode());

        verify(plantRepository).findById(1L);
        verify(plantRepository, never()).save(mockPlant);
    }

    @Test
    void archivePlantById_shouldArchivePlant_ifPlantIsNotArchived() {
        // Arrange
        when(plantRepository.findById(1L)).thenReturn(Optional.of(mockPlant));

        // Act
        plantService.archivePlantById(1L);

        // Assert
        assertNotNull(mockPlant.getArchivedAt());
        verify(plantRepository).findById(1L);
        verify(plantRepository).save(mockPlant);
    }

    @Test
    void archivePlantById_shouldThrowResourceNotFoundException_whenPlantDoesNotExist() {
        // Arrange
        when(plantRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> plantService.archivePlantById(1L));

        verify(plantRepository).findById(1L);
        verify(plantRepository, never()).save(any(Plant.class));
    }

    @Test
    void archivePlantById_shouldThrowConflictException_whenPlantIsAlreadyArchived() {
        // Arrange
        mockPlant.setArchivedAt(LocalDateTime.now());
        when(plantRepository.findById(1L)).thenReturn(Optional.of(mockPlant));

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class, () -> plantService.archivePlantById(1L));

        assertEquals("ALREADY_ARCHIVED", exception.getErrorCode());
        verify(plantRepository).findById(1L);
        verify(plantRepository, never()).save(any(Plant.class));
    }
}
