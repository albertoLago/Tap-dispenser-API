package com.rviewer.skeletons.domain.services;

import com.rviewer.skeletons.domain.entities.DispenserTapUsage;
import com.rviewer.skeletons.domain.events.DispenserStatusChangedEvent;
import com.rviewer.skeletons.domain.services.persistence.DispenserTapUsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class DispenserTapUsageServiceTest {

    @Mock
    private DispenserTapUsageRepository dispenserTapUsageRepository;

    @InjectMocks
    private DispenserTapUsageService dispenserTapUsageService;

    private final long dispenserId = 1L;
    private final double flowVolume = 0.0653;
    private final LocalDateTime updatedAt = LocalDateTime.now();

    @BeforeEach
    void setUp() {
    }

    // Test handling dispenser status change to open
    @Test
    void onOpen_createsUsage() {
        DispenserStatusChangedEvent event = new DispenserStatusChangedEvent(
                new Object(), dispenserId, flowVolume, true, updatedAt);
        DispenserTapUsage tapUsage = new DispenserTapUsage(dispenserId, flowVolume, updatedAt, null);
        when(dispenserTapUsageRepository.save(any(DispenserTapUsage.class))).thenReturn(tapUsage);

        dispenserTapUsageService.handleDispenserStatusChanged(event);

        verify(dispenserTapUsageRepository).save(any(DispenserTapUsage.class));
    }

    // Test handling dispenser status change to closed
    @Test
    void onClose_endsUsage() {
        DispenserStatusChangedEvent event = new DispenserStatusChangedEvent(
                new Object(), dispenserId, flowVolume, false, updatedAt);
        DispenserTapUsage existingTapUsage = new DispenserTapUsage(dispenserId, flowVolume, updatedAt.minusHours(1), null);
        when(dispenserTapUsageRepository.findByDispenserIDAndEndedAtIsNull(dispenserId))
                .thenReturn(Optional.of(existingTapUsage));

        dispenserTapUsageService.handleDispenserStatusChanged(event);

        verify(dispenserTapUsageRepository).findByDispenserIDAndEndedAtIsNull(dispenserId);
        verify(dispenserTapUsageRepository).save(any(DispenserTapUsage.class));
    }
}
