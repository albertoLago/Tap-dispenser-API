package com.rviewer.skeletons.domain.services;

import com.rviewer.skeletons.domain.entities.Dispenser;
import com.rviewer.skeletons.domain.responses.DispenserSpendingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class ServicesIntegrationTest {

    @Autowired
    private DispenserService dispenserService;

    @Autowired
    private FinancialCalculationService financialCalculationService;

    // Test the full lifecycle of dispensers with ongoing usage scenarios, including spending calculation
    @Test
    void testDispensersWithOngoingUsage() {
        Dispenser dispenser1 = dispenserService.createDispenser(0.0653);

        Dispenser dispenser2 = dispenserService.createDispenser(0.075);

        // Simulate the use of the first dispenser: open and close twice, then open and leave ongoing
        LocalDateTime openTime1 = LocalDateTime.now().minusHours(4);
        LocalDateTime closeTime1 = openTime1.plusHours(1);
        LocalDateTime openTime2 = closeTime1.plusHours(1);
        LocalDateTime closeTime2 = openTime2.plusHours(1);
        LocalDateTime openTime3 = closeTime2.plusHours(1);

        // State updates for the first dispenser
        dispenserService.updateDispenserStatus(dispenser1.getId(), true, openTime1);
        dispenserService.updateDispenserStatus(dispenser1.getId(), false, closeTime1);
        dispenserService.updateDispenserStatus(dispenser1.getId(), true, openTime2);
        dispenserService.updateDispenserStatus(dispenser1.getId(), false, closeTime2);
        dispenserService.updateDispenserStatus(dispenser1.getId(), true, openTime3); // Not closed, simulating ongoing use

        // Simulate the use of the second dispenser: open and close once
        LocalDateTime openTime4 = LocalDateTime.now().minusHours(2);
        LocalDateTime closeTime4 = openTime4.plusHours(1);

        // State updates for the second dispenser
        dispenserService.updateDispenserStatus(dispenser2.getId(), true, openTime4);
        dispenserService.updateDispenserStatus(dispenser2.getId(), false, closeTime4);

        // Calculate the spending for the first dispenser
        DispenserSpendingResponse spendingResponse1 = financialCalculationService.getDispenserSpending(dispenser1.getId());

        // Calculate the spending for the second dispenser
        DispenserSpendingResponse spendingResponse2 = financialCalculationService.getDispenserSpending(dispenser2.getId());

        // Verifications for the first dispenser
        assertNotNull(spendingResponse1, "The response should not be null for the first dispenser");
        assertEquals(3, spendingResponse1.getUsages().size(),
                "There should be three usage sessions recorded for the first dispenser, including the ongoing usage");

        // Verifications for the second dispenser
        assertNotNull(spendingResponse2, "The response should not be null for the second dispenser");
        assertEquals(1, spendingResponse2.getUsages().size(),
                "There should be one usage session recorded for the second dispenser");

        // Verify the calculated amount for the first dispenser
        double flowRate1 = 0.0653;
        double timeOpenInSeconds1 = (2 * 3600); // Total open time in seconds for the first dispenser
        double pricePerLiter = 12.25;
        double expectedSpending1 = flowRate1 * timeOpenInSeconds1 * pricePerLiter;

        assertEquals(expectedSpending1, spendingResponse1.getAmount(),
                "The calculated spending should match the expected for the first dispenser");

        // Verify the calculated amount for the second dispenser
        double flowRate2 = 0.075;
        double timeOpenInSeconds2 = 3600; // Total open time in seconds for the second dispenser
        double expectedSpending2 = flowRate2 * timeOpenInSeconds2 * pricePerLiter;

        assertEquals(expectedSpending2, spendingResponse2.getAmount(), "The calculated spending should match the expected for the second dispenser");
    }
}
