package com.rviewer.skeletons.domain.services;

import com.rviewer.skeletons.domain.entities.DispenserTapUsage;
import com.rviewer.skeletons.domain.responses.DispenserSpendingResponse;
import com.rviewer.skeletons.domain.services.persistence.DispenserTapUsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinancialCalculationServiceTest {

    @Mock
    private DispenserTapUsageRepository dispenserTapUsageRepository;

    @InjectMocks
    private FinancialCalculationService financialCalculationService;

    @BeforeEach
    void setUp() {
    }

    // Test the calculation of spending with an ongoing usage scenario
    @Test
    void ongoingUsageSpendingCalc() {
        LocalDateTime startedAt = LocalDateTime.now().minusHours(1);
        long dispenserID = 1L;
        DispenserTapUsage ongoingUsage = new DispenserTapUsage(dispenserID, 0.0653, startedAt, null);
        when(dispenserTapUsageRepository.findByDispenserID(anyLong()))
                .thenReturn(Collections.singletonList(ongoingUsage));

        double expectedVolume = 0.0653 * 3600; // Volume based on 1 hour of usage
        double expectedAmount = expectedVolume * 12.25; // Calculated using the price per liter

        DispenserSpendingResponse response = financialCalculationService.getDispenserSpending(dispenserID);

        assertEquals(expectedAmount, response.getAmount(), "The calculated amount should accurately reflect the ongoing usage based on the current time.");
    }
}
