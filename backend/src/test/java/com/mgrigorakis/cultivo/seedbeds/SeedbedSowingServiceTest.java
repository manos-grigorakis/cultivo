package com.mgrigorakis.cultivo.seedbeds;

import com.mgrigorakis.cultivo.common.exceptions.ConflictException;
import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSowingRequest;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSowingResponse;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSowingStatusRequest;
import com.mgrigorakis.cultivo.seedbeds.mapper.SeedbedSowingMapper;
import com.mgrigorakis.cultivo.seedbeds.model.Seedbed;
import com.mgrigorakis.cultivo.seedbeds.model.SeedbedSlot;
import com.mgrigorakis.cultivo.seedbeds.model.SeedbedSowing;
import com.mgrigorakis.cultivo.seedbeds.model.enums.SeedbedSowingStatus;
import com.mgrigorakis.cultivo.seedbeds.repository.SeedbedSlotRepository;
import com.mgrigorakis.cultivo.seedbeds.repository.SeedbedSowingRepository;
import com.mgrigorakis.cultivo.seedbeds.service.SeedbedSowingServiceImpl;
import com.mgrigorakis.cultivo.seeds.model.SeedPacket;
import com.mgrigorakis.cultivo.seeds.service.SeedPacketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SeedbedSowingServiceTest {
    private SeedbedSlot slot;
    private SeedPacket packet;
    private SeedbedSowing sowing;

    private SeedbedSowingRequest createRequest;
    private SeedbedSowingResponse createResponse;

    private SeedbedSowingRequest updateRequest;
    private SeedbedSowingResponse updateResponse;

    @InjectMocks
    private SeedbedSowingServiceImpl seedbedSowingService;

    @Mock
    private SeedbedSowingRepository seedbedSowingRepository;

    @Mock
    private SeedbedSlotRepository seedbedSlotRepository;

    @Mock
    private SeedbedSowingMapper seedbedSowingMapper;

    @Mock
    private SeedPacketService seedPacketService;

    @BeforeEach
    public void setUp() {
        Seedbed seedbed = Seedbed.builder().name("Seedbed #1").rowsCount(2).columnsCount(3).build();
        seedbed.setId(1L);
        slot = SeedbedSlot.builder().rowIndex(1).columnIndex(1).seedbed(seedbed).build();
        slot.setId(1L);

        packet = SeedPacket.builder().brand("Bayer").supplier("Local Garden Center").code("TOM-CHERRY-001").build();
        packet.setId(1L);

        sowing = SeedbedSowing.builder().seedCount(2).slot(slot).packet(packet).build();
        sowing.setId(1L);

        createRequest = new SeedbedSowingRequest(2, null, null, null, null, packet.getId());
        createResponse = new SeedbedSowingResponse(sowing.getId(), SeedbedSowingStatus.SOWN, 2, null, null, null, null,
                                                   null, null, null, null, null, null);

        updateRequest = new SeedbedSowingRequest(3, null, null, null, null, packet.getId());
        updateResponse = new SeedbedSowingResponse(sowing.getId(), SeedbedSowingStatus.SOWN, 3, null, null, null, null,
                                                   null, null, null, null, null, null);
    }

    @Test
    void getSeedbedSowingByIdAndSeedbedId_shouldReturnSeedbedSowingBySeedbed() {
        // Arrange
        when(seedbedSowingRepository.findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L))
                .thenReturn(Optional.of(sowing));
        when(seedbedSowingMapper.toResponse(sowing)).thenReturn(createResponse);

        // Act
        SeedbedSowingResponse result = seedbedSowingService.getSeedbedSowingByIdAndSeedbedIdAndSlotId(1L, 1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(createResponse, result);
        assertEquals(createResponse.seedCount(), result.seedCount());

        verify(seedbedSowingRepository).findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L);
        verify(seedbedSowingMapper).toResponse(sowing);
    }

    @Test
    void getSeedbedSowingByIdAndSeedbedId_shouldThrowResourceNotFoundException_whenSowingNotFound() {
        // Arrange
        when(seedbedSowingRepository.findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L))
                .thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class, () ->
                seedbedSowingService.getSeedbedSowingByIdAndSeedbedIdAndSlotId(1L, 1L, 1L));

        // Assert
        verify(seedbedSowingRepository).findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L);
        verifyNoInteractions(seedbedSowingMapper);
    }

    @Test
    void getSeedbedSowingById_shouldReturnSeedbedSowingById_whenExists() {
        // Arrange
        when(seedbedSowingRepository.findById(1L)).thenReturn(Optional.of(sowing));
        when(seedbedSowingMapper.toResponse(sowing)).thenReturn(createResponse);

        // Act
        SeedbedSowingResponse result = seedbedSowingService.getSeedbedSowingById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(createResponse, result);
        assertEquals(createResponse.seedCount(), result.seedCount());

        verify(seedbedSowingRepository).findById(1L);
        verify(seedbedSowingMapper).toResponse(sowing);
    }

    @Test
    void getSeedbedSowingById_shouldThrowResourceNotFoundException_whenSowingNotFound() {
        // Arrange
        when(seedbedSowingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class, () -> seedbedSowingService.getSeedbedSowingById(1L));

        // Assert
        verify(seedbedSowingRepository).findById(1L);
        verifyNoInteractions(seedbedSowingMapper);
    }

    @Test
    void createSeedbedSowing_shouldCreateSowing() {
        // Arrange
        when(seedbedSlotRepository.findByIdAndSeedbed_Id(1L, 1L)).thenReturn(Optional.of(slot));
        when(seedPacketService.findSeedPacketById(packet.getId())).thenReturn(packet);
        when(seedbedSowingRepository.existsBySlot_IdAndStatusIn(eq(slot.getId()), anyList())).thenReturn(false);
        when(seedbedSowingMapper.toEntity(createRequest)).thenReturn(sowing);
        when(seedbedSowingRepository.save(sowing)).thenAnswer(i -> i.getArguments()[0]);
        when(seedbedSowingMapper.toResponse(sowing)).thenReturn(createResponse);

        ArgumentCaptor<SeedbedSowing> captor = ArgumentCaptor.forClass(SeedbedSowing.class);

        // Act
        SeedbedSowingResponse result = seedbedSowingService.createSeedbedSowing(1L, 1L, createRequest);

        // Assert
        verify(seedbedSowingRepository).save(captor.capture());

        SeedbedSowing captured = captor.getValue();

        assertEquals(createResponse, result);
        assertEquals(slot, captured.getSlot());
        assertEquals(packet, captured.getPacket());
        assertEquals(SeedbedSowingStatus.SOWN, captured.getStatus());
    }

    @Test
    void createSeedbedSowing_shouldThrowResourceNotFoundException_whenSlotNotFound() {
        // Arrange
        when(seedbedSlotRepository.findByIdAndSeedbed_Id(1L, 1L)).thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class, () ->
                seedbedSowingService.createSeedbedSowing(1L, 1L, createRequest));

        // Assert
        verify(seedbedSlotRepository).findByIdAndSeedbed_Id(1L, 1L);
        verifyNoMoreInteractions(seedbedSlotRepository, seedPacketService, seedbedSowingRepository,
                                 seedbedSowingMapper);
    }

    @Test
    void createSeedbedSowing_shouldThrowResourceNotFoundException_whenSeedPacketNotFound() {
        // Arrange
        when(seedbedSlotRepository.findByIdAndSeedbed_Id(1L, 1L)).thenReturn(Optional.of(slot));
        when(seedPacketService.findSeedPacketById(packet.getId()))
                .thenThrow(new ResourceNotFoundException("Seed packet with id " + packet.getId() + " not found"));

        // Act
        assertThrows(ResourceNotFoundException.class, () ->
                seedbedSowingService.createSeedbedSowing(1L, 1L, createRequest));

        // Assert
        verify(seedPacketService).findSeedPacketById(1L);
        verify(seedbedSlotRepository).findByIdAndSeedbed_Id(1L, 1L);
        verifyNoMoreInteractions(seedbedSlotRepository, seedPacketService, seedbedSowingRepository,
                                 seedbedSowingMapper);
    }

    @Test
    void createSeedbedSowing_shouldThrowConflictException_whenSlotIsAlreadyActive() {
        // Arrange
        when(seedbedSlotRepository.findByIdAndSeedbed_Id(1L, 1L)).thenReturn(Optional.of(slot));
        when(seedPacketService.findSeedPacketById(packet.getId())).thenReturn(packet);
        when(seedbedSowingRepository.existsBySlot_IdAndStatusIn(eq(slot.getId()), anyList())).thenReturn(true);


        // Act
        ConflictException exception = assertThrows(ConflictException.class, () ->
                seedbedSowingService.createSeedbedSowing(1L, 1L, createRequest));

        // Assert
        assertEquals("ACTIVE_SOWING_EXISTS", exception.getErrorCode());

        verify(seedbedSlotRepository).findByIdAndSeedbed_Id(1L, 1L);
        verify(seedPacketService).findSeedPacketById(1L);
        verify(seedbedSowingRepository).existsBySlot_IdAndStatusIn(eq(slot.getId()), anyList());
        verifyNoMoreInteractions(seedbedSlotRepository, seedPacketService, seedbedSowingRepository,
                                 seedbedSowingMapper);
    }

    @Test
    void updateSeedbedSowingById_shouldUpdateSowing() {
        // Arrange
        SeedbedSowing updatedSowing = SeedbedSowing.builder().seedCount(3).slot(slot).packet(packet).build();
        updatedSowing.setId(1L);

        when(seedbedSowingRepository.findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L))
                .thenReturn(Optional.of(sowing));
        when(seedPacketService.findSeedPacketById(packet.getId())).thenReturn(packet);
        when(seedbedSowingMapper.toUpdate(sowing, updateRequest)).thenReturn(updatedSowing);
        when(seedbedSowingRepository.save(updatedSowing)).thenAnswer(i -> i.getArguments()[0]);
        when(seedbedSowingMapper.toResponse(updatedSowing)).thenReturn(updateResponse);

        ArgumentCaptor<SeedbedSowing> captor = ArgumentCaptor.forClass(SeedbedSowing.class);

        // Act
        SeedbedSowingResponse result = seedbedSowingService.updateSeedbedSowingById(1L, 1L, 1L, updateRequest);

        // Assert
        verify(seedbedSowingRepository).save(captor.capture());

        SeedbedSowing captured = captor.getValue();

        assertEquals(result, updateResponse);
        assertEquals(packet, captured.getPacket());
        assertEquals(updateResponse.seedCount(), captured.getSeedCount());

        verify(seedbedSowingRepository).findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L);
        verify(seedPacketService).findSeedPacketById(1L);
        verify(seedbedSowingMapper).toUpdate(sowing, updateRequest);
        verify(seedbedSowingMapper).toResponse(updatedSowing);
    }

    @Test
    void updateSeedbedSowingById_shouldThrowResourceNotFoundException_whenSowingAndSlotAndSeedbedNotFoundById() {
        // Arrange
        when(seedbedSowingRepository.findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L))
                .thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class, () ->
                seedbedSowingService.updateSeedbedSowingById(1L, 1L, 1L, updateRequest));

        // Assert
        verify(seedbedSowingRepository).findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L);
        verifyNoMoreInteractions(seedbedSowingRepository, seedPacketService, seedbedSowingMapper);
    }

    @Test
    void updateSeedbedSowingById_shouldThrowConflictException_whenSowingStatusIsTerminal() {
        // Arrange
        sowing.setStatus(SeedbedSowingStatus.FAILED);

        when(seedbedSowingRepository.findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L))
                .thenReturn(Optional.of(sowing));


        // Act
        ConflictException exception = assertThrows(ConflictException.class, () ->
                seedbedSowingService.updateSeedbedSowingById(1L, 1L, 1L, updateRequest));

        // Assert
        assertEquals("SOWING_TERMINAL_STATUS", exception.getErrorCode());

        verify(seedbedSowingRepository).findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L);
        verifyNoMoreInteractions(seedbedSowingRepository, seedPacketService, seedbedSowingMapper);
    }

    @Test
    void updateSeedbedSowingById_shouldThrowResourceNotFoundException_whenSeedPacketNotFound() {
        // Arrange
        when(seedbedSowingRepository.findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L))
                .thenReturn(Optional.of(sowing));
        when(seedPacketService.findSeedPacketById(packet.getId())).thenThrow(new ResourceNotFoundException(
                "Seed packet with id " + packet.getId() + " not found"));


        // Act
        assertThrows(ResourceNotFoundException.class, () ->
                seedbedSowingService.updateSeedbedSowingById(1L, 1L, 1L, updateRequest));

        // Assert
        verify(seedbedSowingRepository).findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L);
        verify(seedPacketService).findSeedPacketById(1L);
        verifyNoMoreInteractions(seedbedSowingRepository, seedPacketService, seedbedSowingMapper);
    }

    @Test
    void updateSeedbedSowingStatusById_shouldUpdateSowingStatus() {
        // Arrange
        SeedbedSowingStatusRequest request = new SeedbedSowingStatusRequest(SeedbedSowingStatus.GERMINATING);

        sowing.setStatus(SeedbedSowingStatus.SOWN);

        when(seedbedSowingRepository.findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L))
                .thenReturn(Optional.of(sowing));
        when(seedbedSowingRepository.save(any(SeedbedSowing.class))).thenAnswer(i -> i.getArgument(0));

        ArgumentCaptor<SeedbedSowing> captor = ArgumentCaptor.forClass(SeedbedSowing.class);

        // Act
        seedbedSowingService.updateSeedbedSowingStatusById(1L, 1L, 1L, request);

        // Assert
        verify(seedbedSowingRepository).findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L);
        verify(seedbedSowingRepository).save(captor.capture());

        assertEquals(SeedbedSowingStatus.GERMINATING, captor.getValue().getStatus());
        assertNull(captor.getValue().getCompletedAt());
    }

    @Test
    void updateSeedbedSowingStatusById_shouldMarkSowingAsCompleted_whenDesiredStatusIsTerminal() {
        // Arrange
        SeedbedSowingStatusRequest request = new SeedbedSowingStatusRequest(SeedbedSowingStatus.TRANSPLANTED);

        sowing.setStatus(SeedbedSowingStatus.GERMINATED);

        when(seedbedSowingRepository.findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L))
                .thenReturn(Optional.of(sowing));
        when(seedbedSowingRepository.save(any(SeedbedSowing.class))).thenAnswer(i -> i.getArgument(0));

        ArgumentCaptor<SeedbedSowing> captor = ArgumentCaptor.forClass(SeedbedSowing.class);

        // Act
        seedbedSowingService.updateSeedbedSowingStatusById(1L, 1L, 1L, request);

        // Assert
        verify(seedbedSowingRepository).findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L);
        verify(seedbedSowingRepository).save(captor.capture());

        assertEquals(SeedbedSowingStatus.TRANSPLANTED, captor.getValue().getStatus());
        assertNotNull(captor.getValue().getCompletedAt());
    }

    @Test
    void updateSeedbedSowingStatusById_shouldThrowResourceNotFoundException_whenSowingNotFound() {
        // Arrange
        SeedbedSowingStatusRequest request = new SeedbedSowingStatusRequest(SeedbedSowingStatus.TRANSPLANTED);

        when(seedbedSowingRepository.findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L))
                .thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class, () ->
                seedbedSowingService.updateSeedbedSowingStatusById(1L, 1L, 1L, request));

        // Assert
        verify(seedbedSowingRepository).findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L);
        verifyNoMoreInteractions(seedbedSowingRepository);
    }

    @Test
    void updateSeedbedSowingStatusById_shouldConflictException_whenStatusTransitionIsInvalid() {
        // Arrange
        SeedbedSowingStatusRequest request = new SeedbedSowingStatusRequest(SeedbedSowingStatus.GERMINATING);

        sowing.setStatus(SeedbedSowingStatus.GERMINATED);

        when(seedbedSowingRepository.findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L))
                .thenReturn(Optional.of(sowing));

        // Act
        ConflictException exception = assertThrows(ConflictException.class, () ->
                seedbedSowingService.updateSeedbedSowingStatusById(1L, 1L, 1L, request));

        // Assert
        assertEquals("STATUS_TRANSITION_VIOLATION", exception.getErrorCode());

        verify(seedbedSowingRepository).findByIdAndSlot_IdAndSlot_Seedbed_Id(1L, 1L, 1L);
        verifyNoMoreInteractions(seedbedSowingRepository);
    }

    @Test
    void deleteSeedbedSowingById_shouldDeleteSowing() {
        // Arrange
        when(seedbedSowingRepository.findById(1L)).thenReturn(Optional.of(sowing));

        // Act
        seedbedSowingService.deleteSeedbedSowingById(1L);

        // Assert
        verify(seedbedSowingRepository).findById(1L);
        verify(seedbedSowingRepository).delete(sowing);
    }

    @Test
    void deleteSeedbedSowingById_shouldThrowResourceNotFoundException_whenSowingNotFound() {
        // Arrange
        when(seedbedSowingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class, () -> seedbedSowingService.deleteSeedbedSowingById(1L));

        // Assert
        verify(seedbedSowingRepository).findById(1L);
        verifyNoMoreInteractions(seedbedSowingRepository);
    }

    @Test
    void deleteSeedbedSowingById_shouldThrowConflictException_whenSowingStatusIsNotSown() {
        // Arrange
        sowing.setStatus(SeedbedSowingStatus.FAILED);

        when(seedbedSowingRepository.findById(1L)).thenReturn(Optional.of(sowing));

        // Act
        ConflictException exception = assertThrows(ConflictException.class, () ->
                seedbedSowingService.deleteSeedbedSowingById(1L));

        // Assert
        assertEquals("SOWING_DELETION_NOT_ALLOWED", exception.getErrorCode());

        verify(seedbedSowingRepository).findById(1L);
        verifyNoMoreInteractions(seedbedSowingRepository);
    }
}
