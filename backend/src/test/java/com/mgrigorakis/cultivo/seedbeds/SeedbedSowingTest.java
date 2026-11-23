package com.mgrigorakis.cultivo.seedbeds;

import com.mgrigorakis.cultivo.seedbeds.model.SeedbedSowing;
import com.mgrigorakis.cultivo.seedbeds.model.enums.SeedbedSowingStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeedbedSowingTest {
    @Test
    void newSeedbedSowing_shouldHaveSownStatus() {
        // Arrange & Act
        SeedbedSowing sowing = SeedbedSowing.builder().seedCount(2).build();

        // Assert
        assertEquals(SeedbedSowingStatus.SOWN, sowing.getStatus());
    }

    // Runs the same test multiple times with different input values from the CsvSource
    @ParameterizedTest
    @CsvSource({
            "SOWN, false",
            "GERMINATING, false",
            "GERMINATED, false",
            "FAILED, true",
            "TRANSPLANTED, true"
    })
    void isTerminalStatus_shouldReturnExpectedResult(SeedbedSowingStatus status, boolean expected) {
        // Arrange & Act
        SeedbedSowing sowing = SeedbedSowing.builder().build();
        sowing.setStatus(status);

        // Assert
        assertEquals(expected, sowing.isTerminalStatus());
    }

    // Runs the same test multiple times with different input values from the CsvSource
    @ParameterizedTest
    @CsvSource({
            "SOWN, GERMINATING, true",
            "SOWN, FAILED, true",
            "SOWN, TRANSPLANTED, false",
            "GERMINATING, GERMINATED, true",
            "GERMINATING, FAILED, true",
            "GERMINATING, SOWN, false",
            "GERMINATED, TRANSPLANTED, true",
            "GERMINATED, FAILED, true",
            "GERMINATED, SOWN, false",
            "FAILED, SOWN, false",
            "TRANSPLANTED, SOWN, false"
    })
    void canTransitionTo_shouldReturnExpectedResult(SeedbedSowingStatus current, SeedbedSowingStatus target,
                                                    boolean expected) {
        // Arrange & Act
        SeedbedSowing sowing = SeedbedSowing.builder().build();
        sowing.setStatus(current);

        // Assert
        assertEquals(expected, sowing.canTransitionTo(target));
    }
}
