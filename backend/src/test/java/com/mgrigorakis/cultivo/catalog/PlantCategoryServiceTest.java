package com.mgrigorakis.cultivo.catalog;

import com.mgrigorakis.cultivo.common.exceptions.ConflictException;
import com.mgrigorakis.cultivo.common.exceptions.DuplicateEntryException;
import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.catalog.dto.PlantCategoryRequest;
import com.mgrigorakis.cultivo.catalog.dto.PlantCategoryResponse;
import com.mgrigorakis.cultivo.catalog.mapper.PlantCategoryMapper;
import com.mgrigorakis.cultivo.catalog.model.PlantCategory;
import com.mgrigorakis.cultivo.catalog.repository.PlantCategoryRepository;
import com.mgrigorakis.cultivo.catalog.repository.PlantSpeciesRepository;
import com.mgrigorakis.cultivo.catalog.service.PlantCategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlantCategoryServiceTest {
    private PlantCategory mockPlantCategory;
    private PlantCategoryRequest mockRequest;
    private PlantCategoryResponse mockResponse;

    @InjectMocks
    private PlantCategoryServiceImpl plantCategoryService;

    @Mock
    private PlantCategoryRepository plantCategoryRepository;

    @Mock
    private PlantSpeciesRepository plantSpeciesRepository;

    @Mock
    private PlantCategoryMapper plantCategoryMapper;

    @BeforeEach
    public void setUp() {
        mockPlantCategory = PlantCategory.builder().name("Vegetables").build();
        mockRequest = new PlantCategoryRequest("Vegetables", null);
        mockResponse = new PlantCategoryResponse(1L, "Vegetables", null, null, null);
    }

    @Test
    void getPlantCategoryById_shouldReturnPlantCategory_whenExist() {
        // Arrange
        PlantCategoryResponse mockResponse = mock(PlantCategoryResponse.class);

        when(plantCategoryRepository.findById(1L)).thenReturn(Optional.of(mockPlantCategory));
        when(plantCategoryMapper.toResponse(mockPlantCategory)).thenReturn(mockResponse);

        // Act
        PlantCategoryResponse result = plantCategoryService.getPlantCategoryById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(mockResponse, result);

        verify(plantCategoryRepository).findById(1L);
        verify(plantCategoryMapper).toResponse(mockPlantCategory);
    }

    @Test
    void getPlantCategoryById_shouldThrowResourceNotFoundException_whenPlantCategoryDoesNotExist() {
        // Arrange
        when(plantCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> plantCategoryService.getPlantCategoryById(1L));

        verify(plantCategoryRepository).findById(1L);
        verify(plantCategoryMapper, never()).toResponse(any(PlantCategory.class));
    }

    @Test
    void createPlantCategory_shouldCreatePlantCategory() {
        // Arrange
        PlantCategoryRequest mockRequest = new PlantCategoryRequest("Vegetables", null);

        when(plantCategoryRepository.existsByNameIgnoreCase(mockRequest.name())).thenReturn(false);
        when(plantCategoryMapper.toEntity(mockRequest)).thenReturn(mockPlantCategory);
        when(plantCategoryRepository.save(mockPlantCategory)).thenReturn(mockPlantCategory);
        when(plantCategoryMapper.toResponse(mockPlantCategory)).thenReturn(mockResponse);

        // Act
        PlantCategoryResponse result = plantCategoryService.createPlantCategory(mockRequest);

        // Assert
        assertEquals(mockRequest.name(), result.name());
        assertNull(result.description());

        verify(plantCategoryRepository).existsByNameIgnoreCase(mockRequest.name());
        verify(plantCategoryRepository).save(mockPlantCategory);
        verify(plantCategoryMapper).toEntity(mockRequest);
        verify(plantCategoryMapper).toResponse(mockPlantCategory);
    }

    @Test
    void createPlantCategory_shouldThrowDuplicateEntryException_whenPlantCategoryAlreadyExistsByName() {
        // Arrange
        when(plantCategoryRepository.existsByNameIgnoreCase(mockRequest.name())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEntryException.class, () -> plantCategoryService.createPlantCategory(mockRequest));
        verify(plantCategoryRepository).existsByNameIgnoreCase(mockRequest.name());
        verify(plantCategoryMapper, never()).toEntity(any(PlantCategoryRequest.class));
        verify(plantCategoryRepository, never()).save(any(PlantCategory.class));
        verify(plantCategoryMapper, never()).toResponse(any(PlantCategory.class));
    }

    @Test
    void updatePlantCategoryById_shouldUpdatePlantCategory_whenPlantCategoryExists() {
        // Arrange
        PlantCategoryRequest mockUpdateRequest = new PlantCategoryRequest("Herbs", null);
        when(plantCategoryRepository.findById(1L)).thenReturn(Optional.of(mockPlantCategory));
        when(plantCategoryRepository.existsByNameIgnoreCaseAndIdNot(mockUpdateRequest.name(), 1L)).thenReturn(false);
        when(plantCategoryMapper.toUpdate(mockPlantCategory, mockUpdateRequest)).thenReturn(mockPlantCategory);
        when(plantCategoryRepository.save(mockPlantCategory)).thenReturn(mockPlantCategory);
        when(plantCategoryMapper.toResponse(mockPlantCategory)).thenReturn(mockResponse);

        // Act
        PlantCategoryResponse result = plantCategoryService.updatePlantCategoryById(1L, mockUpdateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(mockResponse, result);

        verify(plantCategoryRepository).findById(1L);
        verify(plantCategoryRepository).existsByNameIgnoreCaseAndIdNot(mockUpdateRequest.name(), 1L);
        verify(plantCategoryMapper).toUpdate(mockPlantCategory, mockUpdateRequest);
        verify(plantCategoryRepository).save(mockPlantCategory);
        verify(plantCategoryMapper).toResponse(mockPlantCategory);
    }

    @Test
    void updatePlantCategoryById_shouldThrowResourceNotFoundException_whenPlantCategoryDoesNotExist() {
        // Arrange
        when(plantCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                plantCategoryService.updatePlantCategoryById(1L, mockRequest));

        verify(plantCategoryRepository).findById(1L);
        verifyNoMoreInteractions(plantCategoryRepository);
        verifyNoInteractions(plantCategoryMapper);
    }

    @Test
    void updatePlantCategoryById_shouldThrowDuplicateEntryException_whenPlantCategoryAlreadyExistsByName() {
        // Arrange
        when(plantCategoryRepository.findById(1L)).thenReturn(Optional.of(mockPlantCategory));
        when(plantCategoryRepository.existsByNameIgnoreCaseAndIdNot(mockRequest.name(), 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEntryException.class,
                     () -> plantCategoryService.updatePlantCategoryById(1L, mockRequest));

        verify(plantCategoryRepository).findById(1L);
        verify(plantCategoryRepository).existsByNameIgnoreCaseAndIdNot(mockRequest.name(), 1L);
        verify(plantCategoryRepository, never()).save(any(PlantCategory.class));
        verifyNoInteractions(plantCategoryMapper);
    }

    @Test
    void deletePlantCategoryById_shouldDeletePlantCategory_whenPlantCategoryExists() {
        // Arrange
        when(plantCategoryRepository.findById(1L)).thenReturn(Optional.of(mockPlantCategory));
        when(plantSpeciesRepository.existsByPlantCategoryId(1L)).thenReturn(false);

        // Act
        plantCategoryService.deletePlantCategoryById(1L);

        // Assert
        verify(plantCategoryRepository).findById(1L);
        verify(plantCategoryRepository).delete(mockPlantCategory);
    }

    @Test
    void deletePlantCategoryById_shouldThrowResourceNotFoundException_whenPlantCategoryDoesNotExist() {
        // Arrange
        when(plantCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> plantCategoryService.deletePlantCategoryById(1L));

        verify(plantCategoryRepository).findById(1L);
        verify(plantCategoryRepository, never()).delete(any(PlantCategory.class));
    }

    @Test
    void deletePlantCategoryById_shouldThrowConflictException_whenPlantSpeciesAreAssigned() {
        // Arrange
        when(plantCategoryRepository.findById(1L)).thenReturn(Optional.of(mockPlantCategory));
        when(plantSpeciesRepository.existsByPlantCategoryId(1L)).thenReturn(true);

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class, () ->
                plantCategoryService.deletePlantCategoryById(1L));

        assertEquals("PLANT_CATEGORY_HAS_SPECIES", exception.getErrorCode());

        verify(plantCategoryRepository).findById(1L);
        verify(plantSpeciesRepository).existsByPlantCategoryId(1L);
        verify(plantCategoryRepository, never()).delete(mockPlantCategory);
    }
}
