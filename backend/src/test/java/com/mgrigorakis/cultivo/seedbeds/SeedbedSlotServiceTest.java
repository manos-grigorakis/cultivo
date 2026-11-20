package com.mgrigorakis.cultivo.seedbeds;

import com.mgrigorakis.cultivo.common.exceptions.ResourceNotFoundException;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSlotSummaryResponse;
import com.mgrigorakis.cultivo.seedbeds.mapper.SeedbedSlotMapper;
import com.mgrigorakis.cultivo.seedbeds.model.Seedbed;
import com.mgrigorakis.cultivo.seedbeds.model.SeedbedSlot;
import com.mgrigorakis.cultivo.seedbeds.repository.SeedbedSlotRepository;
import com.mgrigorakis.cultivo.seedbeds.service.SeedbedSlotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SeedbedSlotServiceTest {
    private Seedbed seedbed;
    private SeedbedSlot slot;
    private SeedbedSlotSummaryResponse response;

    @InjectMocks
    private SeedbedSlotServiceImpl seedbedSlotService;

    @Mock
    private SeedbedSlotRepository seedbedSlotRepository;

    @Mock
    private SeedbedSlotMapper seedbedSlotMapper;

    @BeforeEach
    public void setUp() {
        seedbed = Seedbed.builder().name("Seedbed #1").rowsCount(2).columnsCount(3).build();
        seedbed.setId(1L);
        slot = SeedbedSlot.builder().rowIndex(1).columnIndex(1).seedbed(seedbed).build();

        response = new SeedbedSlotSummaryResponse(1L, 1, 1);
    }

    @Test
    void getSeedbedSlotByIdAndSeedbedId_shouldReturnSeedbedSlot() {
        // Arrange
        when(seedbedSlotRepository.findByIdAndSeedbed_Id(1L, 1L)).thenReturn(Optional.of(slot));
        when(seedbedSlotMapper.toSummaryResponse(slot)).thenReturn(response);

        // Act
        SeedbedSlotSummaryResponse result = seedbedSlotService.getSeedbedSlotByIdAndSeedbedId(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(response, result);
        assertEquals(slot.getColumnIndex(), result.columnIndex());

        verify(seedbedSlotRepository).findByIdAndSeedbed_Id(1L, 1L);
        verify(seedbedSlotMapper).toSummaryResponse(slot);
    }

    @Test
    void getSeedbedSlotByIdAndSeedbedId_shouldThrowResourceNotFoundException_whenSlotDoesNotExist() {
        // Arrange
        when(seedbedSlotRepository.findByIdAndSeedbed_Id(1L, 1L)).thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class, () -> seedbedSlotService.getSeedbedSlotByIdAndSeedbedId(1L, 1L));

        // Assert
        verify(seedbedSlotRepository).findByIdAndSeedbed_Id(1L, 1L);
        verifyNoInteractions(seedbedSlotMapper);
    }

    @Test
    void createSeedbedSlotsForSeedbed_shouldCreateSeedbedSlots() {
        // Arrange
        when(seedbedSlotRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));
        when(seedbedSlotMapper.toSummaryResponse(any(SeedbedSlot.class))).thenAnswer(i -> {
                    SeedbedSlot slot = i.getArgument(0);

                    return new SeedbedSlotSummaryResponse(null, slot.getRowIndex(), slot.getColumnIndex()
                    );
                });

        // Act
        List<SeedbedSlotSummaryResponse> result = seedbedSlotService.createSeedbedSlotsForSeedbed(seedbed);

        // Assert
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<SeedbedSlot>> captor = ArgumentCaptor.forClass(List.class);

        verify(seedbedSlotRepository).saveAll(captor.capture());

        List<SeedbedSlot> capturedSlots = captor.getValue();

        assertEquals(6, capturedSlots.size());
        assertEquals(6, result.size());

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 3; col++) {
                int index = row * 3 + col;

                SeedbedSlot capturedSlot = capturedSlots.get(index);

                assertEquals(row, capturedSlot.getRowIndex());
                assertEquals(col, capturedSlot.getColumnIndex());
                assertSame(seedbed, capturedSlot.getSeedbed());
            }
        }

        verify(seedbedSlotMapper, times(6)).toSummaryResponse(any(SeedbedSlot.class));
    }

    @Test
    void deleteAllSeedbedSlotsBySeedbedId_shouldDeleteAllSeedbedSlots() {
        // Act
        seedbedSlotService.deleteAllSeedbedSlotsBySeedbedId(1L);

        // Assert
        verify(seedbedSlotRepository).deleteAllBySeedbed_Id(1L);
    }
}
