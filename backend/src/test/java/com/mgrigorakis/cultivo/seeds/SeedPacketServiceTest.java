package com.mgrigorakis.cultivo.seeds;

import com.mgrigorakis.cultivo.catalog.model.PlantVariety;
import com.mgrigorakis.cultivo.catalog.repository.PlantVarietyRepository;
import com.mgrigorakis.cultivo.common.exceptions.DuplicateEntryException;
import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.seeds.dto.PlantVarietySummary;
import com.mgrigorakis.cultivo.seeds.dto.SeedPacketCreateRequest;
import com.mgrigorakis.cultivo.seeds.dto.SeedPacketResponse;
import com.mgrigorakis.cultivo.seeds.dto.SeedPacketUpdateRequest;
import com.mgrigorakis.cultivo.seeds.mapper.SeedPacketMapper;
import com.mgrigorakis.cultivo.seeds.model.SeedPacket;
import com.mgrigorakis.cultivo.seeds.model.enums.SeedPacketStatus;
import com.mgrigorakis.cultivo.seeds.repository.SeedPacketRepository;
import com.mgrigorakis.cultivo.seeds.service.SeedPacketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SeedPacketServiceTest {
    private PlantVariety plantVariety;
    private PlantVarietySummary plantVarietySummary;

    private SeedPacket createSeedPacket;
    private SeedPacketCreateRequest createRequest;
    private SeedPacketResponse createResponse;

    private SeedPacket updateSeedPacket;
    private SeedPacketUpdateRequest updateRequest;
    private SeedPacketResponse updateResponse;

    @InjectMocks
    private SeedPacketServiceImpl seedPacketService;

    @Mock
    private SeedPacketRepository seedPacketRepository;

    @Mock
    private PlantVarietyRepository plantVarietyRepository;

    @Mock
    private SeedPacketMapper seedPacketMapper;

    @BeforeEach
    public void setUp() {
        plantVariety = PlantVariety.builder().name("Tomato").build();
        plantVariety.setId(1L);
        plantVarietySummary = new PlantVarietySummary(1L, "Tomato");

        createSeedPacket = SeedPacket.builder().brand("Bayer").supplier("Local Garden Center").code("TOM-CHERRY-001").build();
        createSeedPacket.setId(1L);
        createRequest = new SeedPacketCreateRequest("Bayer", "Local Garden Center", "TOM-CHERRY-001", null, null, null, null, null, null, null, null, null, null, 1L);
        createResponse = new SeedPacketResponse(1L, "Bayer", "Local Garden Center", null, "TOM-CHERRY-001", null, null, null, null, null, null, null, null, null, null, null, null, plantVarietySummary);

        updateSeedPacket = SeedPacket.builder().brand("Bayer").supplier("Local Garden Center").code("TOM-CHERRY-002").build();
        updateSeedPacket.setId(1L);
        updateRequest = new SeedPacketUpdateRequest("Bayer", "Local Garden Center", SeedPacketStatus.IN_USE,"TOM-CHERRY-002", null, null, null, null, null, null, null, null, null, null, 1L);
        updateResponse = new SeedPacketResponse(1L, "Bayer", "Local Garden Center", SeedPacketStatus.IN_USE, "TOM-CHERRY-002", null, null, null, null, null, null, null, null, null, null, null, null, plantVarietySummary);
    }

    @Test
    void getSeedPacketById_shouldReturnSeedPacket() {
        // Arrange
        when(seedPacketRepository.findById(1L)).thenReturn(Optional.of(createSeedPacket));
        when(seedPacketMapper.toResponse(createSeedPacket)).thenReturn(createResponse);

        // Act
        SeedPacketResponse result = seedPacketService.getSeedPacketById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(createResponse, result);
        assertEquals(createSeedPacket.getBrand(), result.brand());

        verify(seedPacketRepository).findById(1L);
        verify(seedPacketMapper).toResponse(createSeedPacket);
    }

    @Test
    void getSeedPacketById_shouldThrowResourceNotFoundException_whenSeedPacketNotFound() {
        // Arrange
        when(seedPacketRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> seedPacketService.getSeedPacketById(1L));

        verify(seedPacketRepository).findById(1L);
    }

    @Test
    void createSeedPacket_shouldCreateSeedPacket() {
        // Arrange
        when(seedPacketRepository.existsByPlantVariety_IdAndBrandAndCode(1L, createRequest.brand(), createRequest.code())).thenReturn(false);
        when(plantVarietyRepository.findById(1L)).thenReturn(Optional.of(plantVariety));
        when(seedPacketMapper.toEntity(createRequest)).thenReturn(createSeedPacket);
        when(seedPacketRepository.save(createSeedPacket)).thenAnswer(i -> i.getArgument(0));
        when(seedPacketMapper.toResponse(createSeedPacket)).thenReturn(createResponse);

        // Act
        seedPacketService.createSeedPacket(createRequest);

        // Assert
        ArgumentCaptor<SeedPacket> captor = ArgumentCaptor.forClass(SeedPacket.class);

        verify(seedPacketRepository).save(captor.capture());

        SeedPacket saved = captor.getValue();

        assertEquals(SeedPacketStatus.STORED, saved.getStatus());
        assertEquals(plantVariety, saved.getPlantVariety());

        verify(seedPacketRepository).existsByPlantVariety_IdAndBrandAndCode(1L, createRequest.brand(), createRequest.code());
        verify(plantVarietyRepository).findById(1L);
        verify(seedPacketMapper).toEntity(createRequest);
        verify(seedPacketRepository).save(createSeedPacket);
        verify(seedPacketMapper).toResponse(createSeedPacket);
    }

    @Test
    void createSeedPacket_shouldThrowDuplicateEntryException_whenSeedPackerExistsWithPlantVarietyAndBrandAndCode() {
        // Arrange
        when(seedPacketRepository.existsByPlantVariety_IdAndBrandAndCode(1L, createRequest.brand(), createRequest.code())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEntryException.class, () -> seedPacketService.createSeedPacket(createRequest));

        verify(seedPacketRepository).existsByPlantVariety_IdAndBrandAndCode(1L, createRequest.brand(), createRequest.code());
        verifyNoMoreInteractions(seedPacketRepository, plantVarietyRepository, seedPacketMapper);
    }

    @Test
    void createSeedPacket_shouldThrowResourceNotFoundException_whenPlantVarietyNotFound() {
        // Arrange
        when(seedPacketRepository.existsByPlantVariety_IdAndBrandAndCode(1L, createRequest.brand(), createRequest.code())).thenReturn(false);
        when(plantVarietyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> seedPacketService.createSeedPacket(createRequest));

        verify(seedPacketRepository).existsByPlantVariety_IdAndBrandAndCode(1L, createRequest.brand(), createRequest.code());
        verify(plantVarietyRepository).findById(1L);
        verifyNoMoreInteractions(seedPacketRepository, plantVarietyRepository, seedPacketMapper);
    }

    @Test
    void updateSeedPacketById_shouldUpdateSeedPacket() {
        // Arrange
        when(seedPacketRepository.findById(1L)).thenReturn(Optional.of(updateSeedPacket));
        when(seedPacketRepository.existsByPlantVariety_IdAndBrandAndCodeAndIdNot(1L, updateRequest.brand(), updateRequest.code(), 1L)).thenReturn(false);
        when(plantVarietyRepository.findById(1L)).thenReturn(Optional.of(plantVariety));
        when(seedPacketMapper.toUpdate(updateSeedPacket, updateRequest)).then(i -> {
            SeedPacket packet = i.getArgument(0);
            packet.setStatus(updateRequest.status());
            return packet;
        });
        when(seedPacketRepository.save(updateSeedPacket)).thenAnswer(i -> i.getArgument(0));
        when(seedPacketMapper.toResponse(updateSeedPacket)).thenReturn(updateResponse);

        // Act
        SeedPacketResponse result = seedPacketService.updateSeedPacketById(1L, updateRequest);

        // Assert
        ArgumentCaptor<SeedPacket> captor = ArgumentCaptor.forClass(SeedPacket.class);

        verify(seedPacketRepository).save(captor.capture());

        SeedPacket saved = captor.getValue();

        assertEquals(updateResponse, result);
        assertEquals(SeedPacketStatus.IN_USE, saved.getStatus());
        assertEquals(plantVariety, saved.getPlantVariety());

        verify(seedPacketRepository).findById(1L);
        verify(seedPacketRepository).existsByPlantVariety_IdAndBrandAndCodeAndIdNot(1L, updateRequest.brand(), updateRequest.code(), 1L);
        verify(plantVarietyRepository).findById(1L);
        verify(seedPacketMapper).toUpdate(updateSeedPacket, updateRequest);
        verify(seedPacketMapper).toResponse(saved);
    }

    @Test
    void updateSeedPacketById_shouldThrowResourceNotFoundException_whenSeedPacketNotFound() {
        // Arrange
        when(seedPacketRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> seedPacketService.updateSeedPacketById(1L, updateRequest));

        verify(seedPacketRepository).findById(1L);
        verifyNoMoreInteractions(seedPacketRepository, seedPacketMapper, plantVarietyRepository);
    }

    @Test
    void updateSeedPacketById_shouldThrowDuplicateEntryException_whenSeedPacketExistsWithPlantVarietyAndBrandAndCode() {
        // Arrange
        when(seedPacketRepository.findById(1L)).thenReturn(Optional.of(updateSeedPacket));
        when(seedPacketRepository.existsByPlantVariety_IdAndBrandAndCodeAndIdNot(1L, updateRequest.brand(), updateRequest.code(), 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEntryException.class, () -> seedPacketService.updateSeedPacketById(1L, updateRequest));

        verify(seedPacketRepository).findById(1L);
        verify(seedPacketRepository).existsByPlantVariety_IdAndBrandAndCodeAndIdNot(1L, updateRequest.brand(), updateRequest.code(), 1L);
        verifyNoMoreInteractions(seedPacketRepository, seedPacketMapper, plantVarietyRepository);
    }

    @Test
    void updateSeedPacketById_shouldThrowResourceNotFoundException_whenPlantVarietyNotFound() {
        // Arrange
        when(seedPacketRepository.findById(1L)).thenReturn(Optional.of(updateSeedPacket));
        when(seedPacketRepository.existsByPlantVariety_IdAndBrandAndCodeAndIdNot(1L, updateRequest.brand(), updateRequest.code(), 1L)).thenReturn(false);
        when(plantVarietyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> seedPacketService.updateSeedPacketById(1L, updateRequest));

        verify(seedPacketRepository).findById(1L);
        verify(seedPacketRepository).existsByPlantVariety_IdAndBrandAndCodeAndIdNot(1L, updateRequest.brand(), updateRequest.code(), 1L);
        verify(plantVarietyRepository).findById(1L);
        verifyNoMoreInteractions(seedPacketRepository, seedPacketMapper, plantVarietyRepository);
    }

    @Test
    void deleteSeedPacketById_shouldDeleteSeedPacket() {
        // Arrange
        when(seedPacketRepository.findById(1L)).thenReturn(Optional.of(SeedPacket.builder().build()));

        // Act
        seedPacketService.deleteSeedPacketById(1L);

        // Assert
        verify(seedPacketRepository).findById(1L);
        verify(seedPacketRepository).delete(any(SeedPacket.class));
    }

    @Test
    void deleteSeedPacketById_shouldThrowResourceNotFoundException_whenSeedPacketNotFound() {
        // Arrange
        when(seedPacketRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> seedPacketService.deleteSeedPacketById(1L));

        verify(seedPacketRepository).findById(1L);
        verifyNoMoreInteractions(seedPacketRepository);
    }
}
