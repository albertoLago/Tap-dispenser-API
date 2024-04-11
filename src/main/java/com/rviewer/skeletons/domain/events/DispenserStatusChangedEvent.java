package com.rviewer.skeletons.domain.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
@Setter
public class DispenserStatusChangedEvent extends ApplicationEvent {
    private final long dispenserID;
    private final double flowVolume;
    private final boolean isOpen;
    private final LocalDateTime updatedAt;

    public DispenserStatusChangedEvent(Object source, long dispenserID, double flowVolume, boolean isOpen, LocalDateTime updatedAt) {
        super(source);
        this.dispenserID = dispenserID;
        this.flowVolume = flowVolume;
        this.isOpen = isOpen;
        this.updatedAt = updatedAt;
    }
}
