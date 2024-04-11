package com.rviewer.skeletons.domain.services;

import com.rviewer.skeletons.domain.entities.Dispenser;
import com.rviewer.skeletons.domain.services.persistence.DispenserRepository;
import com.rviewer.skeletons.domain.exceptions.DispenserAlreadyClosedOpenedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DispenserServiceTest {

    @Mock
    private DispenserRepository dispenserRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private DispenserService dispenserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test the successful creation of a dispenser
    @Test
    void createDispenser_success() {
        double flowVolume = 0.0653;
        Dispenser mockDispenser = new Dispenser(flowVolume);
        when(dispenserRepository.save(any(Dispenser.class))).thenReturn(mockDispenser);

        Dispenser createdDispenser = dispenserService.createDispenser(flowVolume);

        assertNotNull(createdDispenser, "The created dispenser should not be null");
        assertEquals(flowVolume, createdDispenser.getFlowVolume(), "The flow volume of the created dispenser should match the input");
    }

    // Test the successful update of a dispenser's status
    @Test
    void updateDispenserStatus_success() {
        long dispenserId = 1L;
        boolean newStatus = true;
        Dispenser mockDispenser = new Dispenser(0.0653);
        mockDispenser.setOpen(false); // Ensure dispenser is initially closed
        when(dispenserRepository.findById(dispenserId)).thenReturn(Optional.of(mockDispenser));
        doNothing().when(eventPublisher).publishEvent(any());

        assertDoesNotThrow(() -> dispenserService.updateDispenserStatus(dispenserId, newStatus, LocalDateTime.now()));

        // Verify that save is called once
        verify(dispenserRepository, times(1)).save(any(Dispenser.class));
    }

    // Test failure when trying to update the status of a dispenser that is already in the desired state
    @Test
    void updateDispenserStatus_failure_alreadyOpen() {
        long dispenserId = 1L;
        // Trying to open an already open dispenser
        boolean newStatus = true;
        Dispenser mockDispenser = new Dispenser(0.0653);
        // Dispenser is already open
        mockDispenser.setOpen(true);
        when(dispenserRepository.findById(dispenserId)).thenReturn(Optional.of(mockDispenser));

        assertThrows(DispenserAlreadyClosedOpenedException.class,
                () -> dispenserService.updateDispenserStatus(dispenserId, newStatus, LocalDateTime.now()),
                "Should throw an exception if trying to open an already open dispenser");
    }
}
