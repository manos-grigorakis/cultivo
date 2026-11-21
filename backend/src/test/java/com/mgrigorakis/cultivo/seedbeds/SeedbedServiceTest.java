package com.mgrigorakis.cultivo.seedbeds;

import com.mgrigorakis.cultivo.common.exceptions.ConflictException;
import com.mgrigorakis.cultivo.common.exceptions.DuplicateEntryException;
import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.seedbeds.dto.*;
import com.mgrigorakis.cultivo.seedbeds.mapper.SeedbedMapper;
import com.mgrigorakis.cultivo.seedbeds.model.Seedbed;
import com.mgrigorakis.cultivo.seedbeds.model.enums.SeedbedLocationType;
import com.mgrigorakis.cultivo.seedbeds.repository.SeedbedRepository;
import com.mgrigorakis.cultivo.seedbeds.repository.SeedbedSowingRepository;
import com.mgrigorakis.cultivo.seedbeds.service.SeedbedServiceImpl;
import com.mgrigorakis.cultivo.seedbeds.service.SeedbedSlotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SeedbedServiceTest {
    private Seedbed seedbed;
    private SeedbedDetailResponse detailResponse;
    private List<SeedbedSlotSummaryResponse> slotSummaryResponseList;
    private SeedbedCreateRequest createRequest;
    private SeedbedUpdateRequest updateRequest;
    private SeedbedResponse updateResponse;

    @InjectMocks
    private SeedbedServiceImpl seedbedService;

    @Mock
    private SeedbedRepository seedbedRepository;

    @Mock
    private SeedbedSowingRepository seedbedSowingRepository;

    @Mock
    private SeedbedMapper seedbedMapper;

    @Mock
    private SeedbedSlotService seedbedSlotService;

    @BeforeEach
    public void setUp() {
        seedbed = Seedbed.builder().name("Seedbed #1").rowsCount(2).columnsCount(3).build();
        seedbed.setId(1L);

        slotSummaryResponseList = List.of(
                new SeedbedSlotSummaryResponse(1L, 0, 0),
                new SeedbedSlotSummaryResponse(1L, 0, 1),
                new SeedbedSlotSummaryResponse(1L, 0, 2),

                new SeedbedSlotSummaryResponse(1L, 1, 0),
                new SeedbedSlotSummaryResponse(1L, 1, 1),
                new SeedbedSlotSummaryResponse(1L, 1, 2)
        );

        detailResponse = new SeedbedDetailResponse(1L, "Seedbed #1", null, 2, 3, null, null, null, false, false, false,
                                                   null, null, null, slotSummaryResponseList);

        createRequest = new SeedbedCreateRequest("Seedbed #1", null, 2, 3, null, null, null, false, false, false, null);

        updateRequest = new SeedbedUpdateRequest("Seedbed #2", SeedbedLocationType.OUTDOOR, null, null, null, false,
                                                 false, false, null);
        updateResponse = new SeedbedResponse(1L, "Seedbed #2", SeedbedLocationType.OUTDOOR, 2, 3, null, null, null,
                                             false, false, false, null, null, null);

    }

    @Test
    void getSeedbedById_shouldReturnSeedbed() {
        // Arrange
        when(seedbedRepository.findById(1L)).thenReturn(Optional.of(seedbed));
        when(seedbedSlotService.getAllSeedbedSlotsBySeedbedId(1L)).thenReturn(slotSummaryResponseList);
        when(seedbedMapper.toResponse(seedbed, slotSummaryResponseList)).thenReturn(detailResponse);

        // Act
        SeedbedDetailResponse result = seedbedService.getSeedbedById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(detailResponse, result);
        assertEquals(seedbed.getName(), result.name());
        assertEquals(slotSummaryResponseList.size(), result.seedbedSlotsSummary().size());

        verify(seedbedRepository).findById(1L);
        verify(seedbedMapper).toResponse(seedbed, slotSummaryResponseList);
    }

    @Test
    void getSeedbedById_shouldThrowResourceNotFoundException_whenSeedbedNotFound() {
        // Arrange
        when(seedbedRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> seedbedService.getSeedbedById(1L));

        verify(seedbedRepository).findById(1L);
        verifyNoInteractions(seedbedSlotService, seedbedMapper);
    }

    @Test
    void createSeedbed_shouldCreateSeedbed_withItsSlots() {
        // Arrange
        when(seedbedRepository.existsByNameIgnoreCase(createRequest.name())).thenReturn(false);
        when(seedbedMapper.toEntity(createRequest)).thenReturn(seedbed);
        when(seedbedRepository.save(seedbed)).thenReturn(seedbed);
        when(seedbedSlotService.createSeedbedSlotsForSeedbed(seedbed)).thenReturn(slotSummaryResponseList);
        when(seedbedMapper.toResponse(seedbed, slotSummaryResponseList)).thenReturn(detailResponse);

        // Act
        SeedbedDetailResponse result = seedbedService.createSeedbed(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(detailResponse, result);
        assertEquals(seedbed.getName(), result.name());
        assertEquals(seedbed.getRowsCount(), result.rowsCount());
        assertEquals(seedbed.getColumnsCount(), result.columnsCount());

        verify(seedbedRepository).existsByNameIgnoreCase(createRequest.name());
        verify(seedbedMapper).toEntity(createRequest);
        verify(seedbedRepository).save(seedbed);
        verify(seedbedSlotService).createSeedbedSlotsForSeedbed(seedbed);
        verify(seedbedMapper).toResponse(seedbed, slotSummaryResponseList);
    }

    @Test
    void createSeedbed_shouldThrowDuplicateEntryException_whenSeedbedAlreadyExistsByName() {
        // Arrange
        when(seedbedRepository.existsByNameIgnoreCase(createRequest.name())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEntryException.class, () -> seedbedService.createSeedbed(createRequest));

        verify(seedbedRepository).existsByNameIgnoreCase(createRequest.name());
        verifyNoMoreInteractions(seedbedRepository, seedbedMapper, seedbedSlotService);
    }

    @Test
    void updateSeedbed_shouldUpdateSeedbed() {
        // Arrange
        when(seedbedRepository.findById(1L)).thenReturn(Optional.of(seedbed));
        when(seedbedRepository.existsByNameIgnoreCaseAndIdNot(updateRequest.name(), 1L)).thenReturn(false);
        when(seedbedMapper.toUpdate(seedbed, updateRequest)).thenReturn(seedbed);
        when(seedbedRepository.save(seedbed)).thenReturn(seedbed);
        when(seedbedMapper.toResponse(seedbed)).thenReturn(updateResponse);

        // Act
        SeedbedResponse result = seedbedService.updateSeedbedById(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(updateResponse, result);
        assertEquals(updateRequest.name(), result.name());
        assertEquals(updateRequest.locationType(), result.locationType());

        verify(seedbedRepository).findById(1L);
        verify(seedbedRepository).existsByNameIgnoreCaseAndIdNot(updateRequest.name(), 1L);
        verify(seedbedMapper).toUpdate(seedbed, updateRequest);
        verify(seedbedRepository).save(seedbed);
        verify(seedbedMapper).toResponse(seedbed);
    }

    @Test
    void updateSeedbed_shouldThrowResourceNotFoundException_whenSeedbedNotFound() {
        // Arrange
        when(seedbedRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> seedbedService.updateSeedbedById(1L, updateRequest));

        verify(seedbedRepository).findById(1L);
        verifyNoMoreInteractions(seedbedRepository, seedbedMapper, seedbedSlotService);
    }

    @Test
    void updateSeedbed_shouldThrowDuplicateEntryException_whenSeedbedAlreadyExistsByName() {
        // Arrange
        when(seedbedRepository.findById(1L)).thenReturn(Optional.of(seedbed));
        when(seedbedRepository.existsByNameIgnoreCaseAndIdNot(updateRequest.name(), 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEntryException.class, () -> seedbedService.updateSeedbedById(1L, updateRequest));

        verify(seedbedRepository).findById(1L);
        verify(seedbedRepository).existsByNameIgnoreCaseAndIdNot(updateRequest.name(), 1L);
        verifyNoMoreInteractions(seedbedRepository, seedbedMapper, seedbedSlotService);
    }

    @Test
    void deleteSeedbedById_shouldDeleteSeedbedAndAllRelatedSeedbedSlots() {
        // Assert
        when(seedbedRepository.findById(1L)).thenReturn(Optional.of(seedbed));
        when(seedbedSowingRepository.existsBySlot_Seedbed_id(1L)).thenReturn(false);

        // Act
        seedbedService.deleteSeedbedById(1L);

        // Assert
        verify(seedbedRepository).findById(1L);
        verify(seedbedSowingRepository).existsBySlot_Seedbed_id(1L);
        verify(seedbedSlotService).deleteAllSeedbedSlotsBySeedbedId(1L);
        verify(seedbedRepository).delete(seedbed);
    }

    @Test
    void deleteSeedbedById_shouldThrowResourceNotFoundException_whenSeedbedNotFound() {
        // Assert
        when(seedbedRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> seedbedService.deleteSeedbedById(1L));

        verify(seedbedRepository).findById(1L);
        verifyNoMoreInteractions(seedbedRepository, seedbedSlotService);
    }

    @Test
    void deleteSeedbedById_shouldThrowConflictException_whenSeedbedSlotsHaveSowings() {
        // Assert
        when(seedbedRepository.findById(1L)).thenReturn(Optional.of(seedbed));
        when(seedbedSowingRepository.existsBySlot_Seedbed_id(1L)).thenReturn(true);

        // Act
        ConflictException exception = assertThrows(ConflictException.class, () -> seedbedService.deleteSeedbedById(1L));

        // Assert
        assertEquals("SOWINGS_EXIST", exception.getErrorCode());

        verify(seedbedRepository).findById(1L);
        verify(seedbedSowingRepository).existsBySlot_Seedbed_id(1L);
        verifyNoMoreInteractions(seedbedRepository, seedbedSlotService);
    }
}
