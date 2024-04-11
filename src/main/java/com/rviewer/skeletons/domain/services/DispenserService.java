package com.rviewer.skeletons.domain.services;

import com.rviewer.skeletons.domain.entities.Dispenser;
import com.rviewer.skeletons.domain.events.DispenserStatusChangedEvent;
import com.rviewer.skeletons.domain.exceptions.DispenserAlreadyClosedOpenedException;
import com.rviewer.skeletons.domain.services.persistence.DispenserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Service for managing operations related to dispensers.
 */
@Service
public class DispenserService {

    private static final Logger log = LoggerFactory.getLogger(DispenserService.class);
    private final DispenserRepository dispenserRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructor for dependency injection.
     *
     * @param dispenserRepository Repository for dispenser database operations.
     * @param eventPublisher Application event publisher.
     */
    @Autowired
    public DispenserService(DispenserRepository dispenserRepository, ApplicationEventPublisher eventPublisher) {
        this.dispenserRepository = dispenserRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Creates a new dispenser with a specific flow volume.
     *
     * @param flowVolume The flow volume of the dispenser (liters per second).
     * @return The created dispenser.
     */
    public Dispenser createDispenser(double flowVolume) {
        Dispenser dispenser = dispenserRepository.save(new Dispenser(flowVolume));
        log.info("Dispenser created with ID: {} and flow volume: {}", dispenser.getId(), flowVolume);
        return dispenser;
    }

    /**
     * Updates the status of a dispenser (open or closed) and records the corresponding event.
     *
     * @param dispenserId The ID of the dispenser to update.
     * @param isOpen The new status of the dispenser (true for open, false for closed).
     * @param updatedAt The date and time of the update.
     */
    @Transactional
    public void updateDispenserStatus(long dispenserId, boolean isOpen, LocalDateTime updatedAt) {
        Dispenser dispenser = dispenserRepository.findById(dispenserId)
                .orElseThrow(() -> new EntityNotFoundException("Dispenser not found"));

        if (dispenser.isOpen() == isOpen) {
            log.error("Attempted to change dispenser status to its current state. Dispenser ID: {}", dispenserId);
            throw new DispenserAlreadyClosedOpenedException("Dispenser is already " + (isOpen ? "opened" : "closed"));
        }

        dispenser.setOpen(isOpen);
        if(isOpen) {
            dispenser.incrementUsageCount();
            dispenser.setLastTapUsageStart(updatedAt);
        }
        else {
            long seconds = ChronoUnit.SECONDS.between(dispenser.getLastTapUsageStart(), updatedAt);
            dispenser.addUsageTime(seconds);
        }

        dispenserRepository.save(dispenser);
        log.info("Dispenser status updated for ID: {}. New status: {}", dispenserId, isOpen ? "open" : "closed");

        eventPublisher.publishEvent(new DispenserStatusChangedEvent(this, dispenserId, dispenser.getFlowVolume(), isOpen, updatedAt));
    }
}