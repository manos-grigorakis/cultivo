package com.mgrigorakis.cultivo.catalog;

import com.mgrigorakis.cultivo.catalog.repository.PlantVarietyRepository;
import com.mgrigorakis.cultivo.common.exceptions.DuplicateEntryException;
import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.catalog.dto.PlantCategorySummary;
import com.mgrigorakis.cultivo.catalog.dto.PlantSpeciesRequest;
import com.mgrigorakis.cultivo.catalog.dto.PlantSpeciesResponse;
import com.mgrigorakis.cultivo.catalog.mapper.PlantSpeciesMapper;
import com.mgrigorakis.cultivo.catalog.model.PlantCategory;
import com.mgrigorakis.cultivo.catalog.model.PlantSpecies;
import com.mgrigorakis.cultivo.catalog.repository.PlantCategoryRepository;
import com.mgrigorakis.cultivo.catalog.repository.PlantSpeciesRepository;
import com.mgrigorakis.cultivo.catalog.service.PlantSpeciesServiceImpl;
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
public class PlantSpeciesServiceTest {
    private PlantSpecies mockPlantSpecies;
    private PlantSpecies mockPlantSpeciesUpdate;
    private PlantCategory mockPlantCategory;

    private PlantSpeciesRequest mockCreateRequest;
    private PlantSpeciesRequest mockUpdateRequest;
    private PlantSpeciesResponse mockCreateResponse;
    private PlantSpeciesResponse mockUpdateResponse;

    @InjectMocks
    private PlantSpeciesServiceImpl plantSpeciesService;

    @Mock
    private PlantSpeciesRepository plantSpeciesRepository;

    @Mock
    private PlantCategoryRepository plantCategoryRepository;

    @Mock
    private PlantVarietyRepository plantVarietyRepository;

    @Mock
    private PlantSpeciesMapper plantSpeciesMapper;

    @BeforeEach
    public void setUp() {
        mockPlantCategory = PlantCategory.builder().name("Herbs").build();
        mockPlantCategory.setId(1L);

        mockPlantSpecies = PlantSpecies.builder().name("Basil").scientificName("Ocimum basilicum").plantCategory(mockPlantCategory).build();
        mockPlantSpeciesUpdate = PlantSpecies.builder().name("Tomato").scientificName("Solanum Lycopersicum").plantCategory(mockPlantCategory).build();
        PlantCategorySummary mockPlantCategorySummary = new PlantCategorySummary(1L, "Herbs");

        mockCreateRequest = new PlantSpeciesRequest("Basil", "Ocimum basilicum", null, null, null, null, 1L);
        mockUpdateRequest = new PlantSpeciesRequest("Tomato", "Solanum Lycopersicum", null, null, null, null, 1L);
        mockCreateResponse = new PlantSpeciesResponse(1L, "Basil", "Ocimum basilicum", null, null, null, null, null, null, mockPlantCategorySummary);
        mockUpdateResponse = new PlantSpeciesResponse(1L, "Tomato", "Solanum Lycopersicum", null, null, null, null, null, null, mockPlantCategorySummary);
    }

    @Test
    void getPlantSpeciesById_shouldReturnPlantSpecies_whenExists() {
        // Arrange
        when(plantSpeciesRepository.findWithPlantCategoryById(1L)).thenReturn(Optional.of(mockPlantSpecies));
        when(plantSpeciesMapper.toResponse(mockPlantSpecies)).thenReturn(mockCreateResponse);

        // Act
        PlantSpeciesResponse result  = plantSpeciesService.getPlantSpeciesById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(mockCreateResponse, result);
        verify(plantSpeciesRepository).findWithPlantCategoryById(1L);
        verify(plantSpeciesMapper).toResponse(mockPlantSpecies);
    }

    @Test
    void getPlantSpeciesById_shouldThrowResourceNotFoundException_whenNotExists() {
        // Arrange
        when(plantSpeciesRepository.findWithPlantCategoryById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> plantSpeciesService.getPlantSpeciesById(1L));

        verify(plantSpeciesRepository).findWithPlantCategoryById(1L);
        verify(plantSpeciesMapper, never()).toResponse(mockPlantSpecies);
    }

    @Test
    void createPlantSpecies_shouldCreatePlantSpecies() {
        // Arrange
        when(plantSpeciesRepository.existsByNameIgnoreCase(mockPlantSpecies.getName())).thenReturn(false);
        when(plantSpeciesRepository.existsByScientificNameIgnoreCase(mockPlantSpecies.getScientificName())).thenReturn(false);
        when(plantCategoryRepository.findById(1L)).thenReturn(Optional.of(mockPlantCategory));
        when(plantSpeciesMapper.toEntity(mockCreateRequest)).thenReturn(mockPlantSpecies);
        when(plantSpeciesRepository.save(mockPlantSpecies)).thenReturn(mockPlantSpecies);
        when(plantSpeciesMapper.toResponse(mockPlantSpecies)).thenReturn(mockCreateResponse);

        // Act
        PlantSpeciesResponse result = plantSpeciesService.createPlantSpecies(mockCreateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(mockCreateResponse, result);

        verify(plantSpeciesRepository).existsByNameIgnoreCase(mockPlantSpecies.getName());
        verify(plantSpeciesRepository).existsByScientificNameIgnoreCase(mockPlantSpecies.getScientificName());
        verify(plantCategoryRepository).findById(1L);
        verify(plantSpeciesMapper).toEntity(mockCreateRequest);
        verify(plantSpeciesRepository).save(mockPlantSpecies);
        verify(plantSpeciesMapper).toResponse(mockPlantSpecies);
    }

    @Test
    void createPlantSpecies_shouldThrowDuplicateException_whenNameAlreadyExists() {
        // Arrange
        when(plantSpeciesRepository.existsByNameIgnoreCase(mockPlantSpecies.getName())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEntryException.class, () -> plantSpeciesService.createPlantSpecies(mockCreateRequest));

        verify(plantSpeciesRepository).existsByNameIgnoreCase(mockPlantSpecies.getName());
        verifyNoMoreInteractions(plantSpeciesRepository, plantCategoryRepository,  plantSpeciesMapper);
    }

    @Test
    void createPlantSpecies_shouldThrowResourceNotFoundException_whenPlantCategoryNotExists() {
        // Arrange
        when(plantSpeciesRepository.existsByNameIgnoreCase(mockPlantSpecies.getName())).thenReturn(false);
        when(plantSpeciesRepository.existsByScientificNameIgnoreCase(mockPlantSpecies.getScientificName())).thenReturn(false);
        when(plantCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> plantSpeciesService.createPlantSpecies(mockCreateRequest));

        verify(plantSpeciesRepository).existsByNameIgnoreCase(mockPlantSpecies.getName());
        verify(plantSpeciesRepository).existsByScientificNameIgnoreCase(mockPlantSpecies.getScientificName());
        verify(plantCategoryRepository).findById(1L);
        verifyNoMoreInteractions(plantSpeciesRepository, plantCategoryRepository,  plantSpeciesMapper);
    }

    @Test
    void updatePlantSpeciesById_shouldUpdatePlantSpecies() {
        // Arrange
        when(plantSpeciesRepository.findWithPlantCategoryById(1L)).thenReturn(Optional.of(mockPlantSpecies));
        when(plantSpeciesRepository.existsByNameIgnoreCaseAndIdNot(mockUpdateRequest.name(), 1L)).thenReturn(false);
        when(plantSpeciesRepository.existsByScientificNameIgnoreCaseAndIdNot(mockUpdateRequest.scientificName(), 1L)).thenReturn(false);
        when(plantCategoryRepository.findById(1L)).thenReturn(Optional.of(mockPlantCategory));
        when(plantSpeciesMapper.toUpdate(mockPlantSpecies, mockUpdateRequest)).thenReturn(mockPlantSpeciesUpdate);
        when(plantSpeciesRepository.save(mockPlantSpeciesUpdate)).thenReturn(mockPlantSpeciesUpdate);
        when(plantSpeciesMapper.toResponse(mockPlantSpeciesUpdate)).thenReturn(mockUpdateResponse);

        // Act
        PlantSpeciesResponse result = plantSpeciesService.updatePlantSpeciesById(1L, mockUpdateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(mockUpdateResponse, result);
        assertEquals("Tomato", result.name());

        verify(plantSpeciesRepository).findWithPlantCategoryById(1L);
        verify(plantSpeciesRepository).existsByNameIgnoreCaseAndIdNot(mockUpdateRequest.name(), 1L);
        verify(plantSpeciesRepository).existsByScientificNameIgnoreCaseAndIdNot(mockUpdateRequest.scientificName(), 1L);
        verify(plantCategoryRepository).findById(1L);
        verify(plantSpeciesMapper).toUpdate(mockPlantSpecies, mockUpdateRequest);
        verify(plantSpeciesRepository).save(mockPlantSpeciesUpdate);
        verify(plantSpeciesMapper).toResponse(mockPlantSpeciesUpdate);
    }

    @Test
    void updatePlantSpeciesById_shouldThrowResourceNotFoundException_whenPlantSpeciesNotExists() {
        // Arrange
        when(plantSpeciesRepository.findWithPlantCategoryById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> plantSpeciesService.updatePlantSpeciesById(1L, mockUpdateRequest));

        verify(plantSpeciesRepository).findWithPlantCategoryById(1L);
        verifyNoMoreInteractions(plantSpeciesRepository, plantCategoryRepository,  plantSpeciesMapper);
    }

    @Test
    void updatePlantSpeciesById_shouldThrowResourceNotFoundException_whenPlantCategoryNotExists() {
        // Arrange
        when(plantSpeciesRepository.findWithPlantCategoryById(1L)).thenReturn(Optional.of(mockPlantSpecies));
        when(plantSpeciesRepository.existsByNameIgnoreCaseAndIdNot(mockUpdateRequest.name(), 1L)).thenReturn(false);
        when(plantSpeciesRepository.existsByScientificNameIgnoreCaseAndIdNot(mockUpdateRequest.scientificName(), 1L)).thenReturn(false);
        when(plantCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> plantSpeciesService.updatePlantSpeciesById(1L, mockUpdateRequest));

        verify(plantSpeciesRepository).findWithPlantCategoryById(1L);
        verify(plantSpeciesRepository).existsByNameIgnoreCaseAndIdNot(mockUpdateRequest.name(), 1L);
        verify(plantSpeciesRepository).existsByScientificNameIgnoreCaseAndIdNot(mockUpdateRequest.scientificName(), 1L);
        verify(plantCategoryRepository).findById(1L);
        verifyNoMoreInteractions(plantSpeciesRepository, plantCategoryRepository,  plantSpeciesMapper);
    }

    @Test
    void updatePlantSpeciesById_shouldThrowDuplicateException_whenScientificNameAlreadyExists() {
        // Arrange
        when(plantSpeciesRepository.findWithPlantCategoryById(1L)).thenReturn(Optional.of(mockPlantSpecies));
        when(plantSpeciesRepository.existsByNameIgnoreCaseAndIdNot(mockUpdateRequest.name(), 1L)).thenReturn(false);
        when(plantSpeciesRepository.existsByScientificNameIgnoreCaseAndIdNot(mockUpdateRequest.scientificName(), 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEntryException.class, () -> plantSpeciesService.updatePlantSpeciesById(1L, mockUpdateRequest));

        verify(plantSpeciesRepository).findWithPlantCategoryById(1L);
        verify(plantSpeciesRepository).existsByNameIgnoreCaseAndIdNot(mockUpdateRequest.name(), 1L);
        verify(plantSpeciesRepository).existsByScientificNameIgnoreCaseAndIdNot(mockUpdateRequest.scientificName(), 1L);
        verifyNoMoreInteractions(plantSpeciesRepository, plantCategoryRepository,  plantSpeciesMapper);
    }

    @Test
    void deletePlantSpeciesById_shouldDeletePlantSpecies_whenExists() {
        // Arrange
        when(plantSpeciesRepository.findById(1L)).thenReturn(Optional.of(mockPlantSpecies));
        when(plantVarietyRepository.existsByPlantSpeciesId(1L)).thenReturn(false);

        // Act
        plantSpeciesService.deletePlantSpeciesById(1L);

        // Assert
        verify(plantSpeciesRepository).findById(1L);
        verify(plantSpeciesRepository).delete(mockPlantSpecies);
    }

    @Test
    void deletePlantSpeciesById_shouldThrowResourceNotFoundException_whenNotExists() {
        // Arrange
        when(plantSpeciesRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> plantSpeciesService.deletePlantSpeciesById(1L));

        verify(plantSpeciesRepository).findById(1L);
        verifyNoMoreInteractions(plantSpeciesRepository);
    }
}
