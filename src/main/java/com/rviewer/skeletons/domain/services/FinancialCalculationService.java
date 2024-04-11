package com.rviewer.skeletons.domain.services;

import com.rviewer.skeletons.domain.entities.DispenserTapUsage;
import com.rviewer.skeletons.domain.responses.DispenserSpendingResponse;
import com.rviewer.skeletons.domain.services.persistence.DispenserTapUsageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for calculating and providing financial information
 * regarding the usage of dispensers. It computes the total spending based on
 * the volume of liquid dispensed and the predefined price per liter.
 */
@Service
public class FinancialCalculationService {
    private static final Logger log = LoggerFactory.getLogger(FinancialCalculationService.class);
    private final DispenserTapUsageRepository dispenserTapUsageRepository;
    private static final double PRICE_PER_LITER = 12.25;

    /**
     * Constructs a FinancialCalculationService with the necessary repository.
     *
     * @param dispenserTapUsageRepository Repository for managing dispenser tap usage data.
     */
    @Autowired
    public FinancialCalculationService(DispenserTapUsageRepository dispenserTapUsageRepository) {
        this.dispenserTapUsageRepository = dispenserTapUsageRepository;
    }

    /**
     * Calculates and returns the total spending for a given dispenser ID,
     * including detailed spending for each usage session.
     *
     * @param dispenserID The ID of the dispenser.
     * @return A response object containing the total spending and a list of usage lines.
     */
    public DispenserSpendingResponse getDispenserSpending(long dispenserID) {
        log.info("Calculating spending for dispenser ID: {}", dispenserID);
        List<DispenserTapUsage> dispenserTapUsages = dispenserTapUsageRepository.findByDispenserID(dispenserID);
        List<DispenserSpendingResponse.UsageLine> usageLines = new ArrayList<>();
        double totalSpending = 0.0;

        for (DispenserTapUsage tapUsage : dispenserTapUsages) {
            String endedAtStr = tapUsage.getEndedAt() != null ? tapUsage.getEndedAt().toString() : null;
            double cost = calculateCost(tapUsage);
            DispenserSpendingResponse.UsageLine usageLine = new DispenserSpendingResponse.UsageLine(
                    tapUsage.getStartedAt().toString(), endedAtStr, tapUsage.getFlowVolume(), cost);
            usageLines.add(usageLine);
            totalSpending += cost;
        }

        log.info("Total spending for dispenser ID: {} is {}", dispenserID, totalSpending);
        return new DispenserSpendingResponse(totalSpending, usageLines);
    }

    /**
     * Calculates the cost of a single tap usage session.
     *
     * @param tapUsage The tap usage session.
     * @return The cost of the session based on the volume and the price per liter.
     */
    private double calculateCost(DispenserTapUsage tapUsage) {
        double volume = tapUsage.getVolume() > 0 ? tapUsage.getVolume() : calculateCurrentVolume(tapUsage);
        return volume * PRICE_PER_LITER;
    }

    /**
     * Calculates the current volume of a tap usage session, useful for sessions still in progress.
     *
     * @param tapUsage The tap usage session.
     * @return The calculated volume based on the time elapsed and the flow rate.
     */
    private double calculateCurrentVolume(DispenserTapUsage tapUsage) {
        LocalDateTime endTime = tapUsage.getEndedAt() != null ? tapUsage.getEndedAt() : LocalDateTime.now();
        long seconds = ChronoUnit.SECONDS.between(tapUsage.getStartedAt(), endTime);
        return seconds * tapUsage.getFlowVolume();
    }
}
