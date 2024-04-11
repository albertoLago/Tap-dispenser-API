package com.rviewer.skeletons.domain.services;

import com.rviewer.skeletons.domain.entities.DispenserTapUsage;
import com.rviewer.skeletons.domain.events.DispenserStatusChangedEvent;
import com.rviewer.skeletons.domain.services.persistence.DispenserTapUsageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Service responsible for managing dispenser tap usage records.
 * It listens to dispenser status change events and creates or ends tap usage sessions accordingly.
 */
@Service
public class DispenserTapUsageService {

    private static final Logger log = LoggerFactory.getLogger(DispenserTapUsageService.class);
    private final DispenserTapUsageRepository dispenserTapUsageRepository;

    /**
     * Constructs a DispenserTapUsageService with the necessary repository.
     *
     * @param dispenserTapUsageRepository Repository for managing dispenser tap usage data.
     */
    @Autowired
    public DispenserTapUsageService(DispenserTapUsageRepository dispenserTapUsageRepository) {
        this.dispenserTapUsageRepository = dispenserTapUsageRepository;
    }

    /**
     * Handles dispenser status changes by either creating a new tap usage session
     * or ending an existing one based on the dispenser's new status.
     *
     * @param event The event containing details about the dispenser's status change.
     */
    @Transactional
    @EventListener
    public void handleDispenserStatusChanged(DispenserStatusChangedEvent event) {
        if (event.isOpen()) {
            log.info("Creating tap usage for dispenser ID: {}, flow volume: {}, at {}", event.getDispenserID(), event.getFlowVolume(), event.getUpdatedAt());
            createDispenserTapUsage(event.getDispenserID(), event.getFlowVolume(), event.getUpdatedAt());
        } else {
            log.info("Ending tap usage for dispenser ID: {} at {}", event.getDispenserID(), event.getUpdatedAt());
            endDispenserTapUsage(event.getDispenserID(), event.getUpdatedAt());
        }
    }

    /**
     * Creates a new dispenser tap usage session.
     *
     * @param dispenserID The ID of the dispenser.
     * @param flowVolume The flow volume rate of the dispenser (liters per second).
     * @param updatedAt The timestamp when the tap was opened.
     */
    public void createDispenserTapUsage(long dispenserID, double flowVolume, LocalDateTime updatedAt) {
        DispenserTapUsage dispenserTapUsage = new DispenserTapUsage(dispenserID, flowVolume, updatedAt, null);
        dispenserTapUsageRepository.save(dispenserTapUsage);
        log.info("Dispenser tap usage created for ID: {}", dispenserID);
    }

    /**
     * Ends an existing dispenser tap usage session.
     *
     * @param dispenserID The ID of the dispenser whose tap usage session is to be ended.
     * @param updatedAt The timestamp when the tap was closed.
     */
    public void endDispenserTapUsage(long dispenserID, LocalDateTime updatedAt) {
        Optional<DispenserTapUsage> currentDispenserTapUsage =
                dispenserTapUsageRepository.findByDispenserIDAndEndedAtIsNull(dispenserID);
        if (!currentDispenserTapUsage.isPresent()) {
            log.error("Attempted to end tap usage for dispenser ID: {} without an active session", dispenserID);
            throw new IllegalStateException("No active tap for this dispenser.");
        }

        DispenserTapUsage dispenserTapUsage = currentDispenserTapUsage.get();
        dispenserTapUsage.setEndedAt(updatedAt);
        long seconds = ChronoUnit.SECONDS.between(dispenserTapUsage.getStartedAt(), updatedAt);
        dispenserTapUsage.setVolume(seconds * dispenserTapUsage.getFlowVolume());
        dispenserTapUsageRepository.save(dispenserTapUsage);
        log.info("Dispenser tap usage ended for ID: {}", dispenserID);
    }
}
