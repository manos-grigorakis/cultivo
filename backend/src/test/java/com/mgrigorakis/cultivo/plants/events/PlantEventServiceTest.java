package com.mgrigorakis.cultivo.plants.events;

import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.common.exceptions.ConflictException;
import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.plants.events.dto.PlantEventRequest;
import com.mgrigorakis.cultivo.plants.events.dto.PlantEventResponse;
import com.mgrigorakis.cultivo.plants.events.mapper.PlantEventMapper;
import com.mgrigorakis.cultivo.plants.events.model.PlantEvent;
import com.mgrigorakis.cultivo.plants.events.model.enums.PlantEventType;
import com.mgrigorakis.cultivo.plants.events.repository.PlantEventRepository;
import com.mgrigorakis.cultivo.plants.events.service.PlantEventServiceImpl;
import com.mgrigorakis.cultivo.plants.model.Plant;
import com.mgrigorakis.cultivo.plants.repository.PlantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlantEventServiceTest {
    @InjectMocks
    private PlantEventServiceImpl plantEventService;

    @Mock
    private PlantEventRepository plantEventRepository;

    @Mock
    private PlantRepository plantRepository;

    @Mock
    private PlantEventMapper plantEventMapper;

    @Test
    void getAllPlantEventsByPlantId_shouldThrowResourceNotFoundException_whenPlantDoesNotExist() {
        // Arrange
        PageFilterRequest mockFilterRequest = mock(PageFilterRequest.class);
        PageSortRequest mockSortRequest = mock(PageSortRequest.class);

        when(plantRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                plantEventService.getAllPlantEventsByPlantId(1L, mockFilterRequest, mockSortRequest));

        verify(plantRepository).findById(1L);
        verifyNoInteractions(plantEventRepository, plantEventMapper);
    }

    @Test
    void getPlantEventByIdAndPlantId_shouldReturnPlantEvent_whenPlantEventAndPlantExist() {
        // Arrange
        PlantEvent mockPlantEvent = PlantEvent.builder().title("Watering").build();
        PlantEventResponse mockResponse = mock(PlantEventResponse.class);

        when(plantEventRepository.findByIdAndPlantId(1L, 1L)).thenReturn(Optional.of(mockPlantEvent));
        when(plantEventMapper.toResponse(mockPlantEvent)).thenReturn(mockResponse);

        // Act
        PlantEventResponse result = plantEventService.getPlantEventByIdAndPlantId(1L, 1L);

        // Assert
        assertSame(mockResponse, result);
        verify(plantEventRepository).findByIdAndPlantId(1L, 1L);
        verify(plantEventMapper).toResponse(mockPlantEvent);
    }

    @Test
    void getPlantEventByIdAndPlantId_shouldThrowResourceNotFoundException_whenPlantEventDoesNotExist() {
        // Arrange
        when(plantEventRepository.findByIdAndPlantId(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> plantEventService.getPlantEventByIdAndPlantId(1L, 1L));

        verify(plantEventRepository).findByIdAndPlantId(1L, 1L);
        verify(plantEventMapper, never()).toResponse(any(PlantEvent.class));
    }

    @Test
    void createPlantEvent_shouldCreatePlantEvent_whenPlantExist() {
        // Arrange
        PlantEventRequest mockRequest = new PlantEventRequest("Watering", null, PlantEventType.WATERING, null);
        Plant mockPlant = Plant.builder().label("Basil").build();
        PlantEvent mockPlantEvent = PlantEvent.builder().title("Watering").type(PlantEventType.WATERING).build();
        PlantEventResponse mockResponse = mock(PlantEventResponse.class);

        when(plantRepository.findById(1L)).thenReturn(Optional.of(mockPlant));
        when(plantEventMapper.toEntity(mockRequest)).thenReturn(mockPlantEvent);
        when(plantEventRepository.save(mockPlantEvent)).thenReturn(mockPlantEvent);
        when(plantEventMapper.toResponse(mockPlantEvent)).thenReturn(mockResponse);

        // Act
        PlantEventResponse result = plantEventService.createPlantEvent(1L, mockRequest);

        // Assert
        assertEquals(mockResponse, result);
        assertSame(mockPlant, mockPlantEvent.getPlant());

        verify(plantRepository).findById(1L);
        verify(plantEventMapper).toEntity(mockRequest);
        verify(plantEventRepository).save(mockPlantEvent);
        verify(plantEventMapper).toResponse(mockPlantEvent);
    }

    @Test
    void createPlantEvent_shouldThrowResourceNotFoundException_whenPlantDoesNotExist() {
        // Arrange
        PlantEventRequest mockRequest = new PlantEventRequest("Watering", null, PlantEventType.WATERING, null);
        when(plantRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> plantEventService.createPlantEvent(1L, mockRequest));

        verify(plantRepository).findById(1L);
        verify(plantEventRepository, never()).save(any(PlantEvent.class));
    }

    @Test
    void createPlantEvent_shouldThrowConflictException_whenPlantIsArchived() {
        // Arrange
        PlantEventRequest mockRequest = new PlantEventRequest("Watering", null, PlantEventType.WATERING, null);
        Plant mockPlant = Plant.builder().label("Basil").build();
        mockPlant.setArchivedAt(LocalDateTime.now());

        when(plantRepository.findById(1L)).thenReturn(Optional.of(mockPlant));

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class, () ->
                plantEventService.createPlantEvent(1L, mockRequest));

        assertEquals("PLANT_ARCHIVED", exception.getErrorCode());

        verify(plantRepository).findById(1L);
        verify(plantEventRepository, never()).save(any(PlantEvent.class));
    }

    @Test
    void updatePlantEventByIdAndPlantId_shouldUpdatePlantEvent_whenPlantIdAndPlantIdExist() {
        // Arrange
        PlantEventRequest mockRequest = new PlantEventRequest("Watering", null, PlantEventType.WATERING, null);
        PlantEventResponse mockResponse = mock(PlantEventResponse.class);
        Plant mockPlant = Plant.builder().label("Basil").build();
        PlantEvent mockPlantEvent = PlantEvent.builder().title("Note #1").type(PlantEventType.NOTE).build();
        mockPlantEvent.setPlant(mockPlant);

        when(plantEventRepository.findByIdAndPlantId(1L, 1L)).thenReturn(Optional.of(mockPlantEvent));
        when(plantEventMapper.toUpdate(mockPlantEvent, mockRequest)).thenReturn(mockPlantEvent);
        when(plantEventRepository.save(mockPlantEvent)).thenReturn(mockPlantEvent);
        when(plantEventMapper.toResponse(mockPlantEvent)).thenReturn(mockResponse);

        // Act
        PlantEventResponse result = plantEventService.updatePlantEventByIdAndPlantId(1L, 1L, mockRequest);

        // Assert
        assertNotNull(result);
        assertEquals(mockResponse, result);

        verify(plantEventRepository).findByIdAndPlantId(1L, 1L);
        verify(plantEventMapper).toUpdate(mockPlantEvent, mockRequest);
        verify(plantEventRepository).save(mockPlantEvent);
        verify(plantEventMapper).toResponse(mockPlantEvent);
    }

    @Test
    void updatePlantEventByIdAndPlantId_shouldThrowResourceNotFoundException_whenPlantIdAndPlantEventIdDoesNotExist() {
        // Arrange
        when(plantEventRepository.findByIdAndPlantId(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                     () -> plantEventService.updatePlantEventByIdAndPlantId(1L, 1L, mock(PlantEventRequest.class)));

        verify(plantEventRepository).findByIdAndPlantId(1L, 1L);
        verify(plantEventMapper, never()).toUpdate(any(PlantEvent.class), any(PlantEventRequest.class));
        verify(plantEventRepository, never()).save(any(PlantEvent.class));
        verify(plantEventMapper, never()).toResponse(any(PlantEvent.class));
    }

    @Test
    void updatePlantEventByIdAndPlantId_shouldThrowConflictException_whenPlantIsArchived() {
        // Arrange
        Plant mockPlant = Plant.builder().label("Basil").build();
        PlantEvent mockPlantEvent = PlantEvent.builder().title("Note #1").type(PlantEventType.NOTE).build();
        mockPlant.setArchivedAt(LocalDateTime.now());
        mockPlantEvent.setPlant(mockPlant);

        when(plantEventRepository.findByIdAndPlantId(1L, 1L)).thenReturn(Optional.of(mockPlantEvent));

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class, () ->
                plantEventService.updatePlantEventByIdAndPlantId(1L, 1L, mock(PlantEventRequest.class)));

        assertEquals("PLANT_ARCHIVED", exception.getErrorCode());

        verify(plantEventRepository).findByIdAndPlantId(1L, 1L);
        verify(plantEventMapper, never()).toUpdate(any(PlantEvent.class), any(PlantEventRequest.class));
        verify(plantEventRepository, never()).save(any(PlantEvent.class));
        verify(plantEventMapper, never()).toResponse(any(PlantEvent.class));
    }
}
