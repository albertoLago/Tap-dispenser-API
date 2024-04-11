package com.rviewer.skeletons.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Dispenser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "is_open", nullable = false)
    private boolean isOpen;

    @NotNull(message = "Flow volume must not be null")
    @Positive(message = "Flow volume must be positive")
    @Column(name = "flow_volume", nullable = false)
    private double flowVolume;

    @Column(name = "usage_count", nullable = false)
    private int usageCount = 0;

    @Column(name = "total_usage_time_in_seconds", nullable = false)
    private long totalUsageTimeInSeconds = 0;

    @Column(name = "last_tap_usage_start")
    private LocalDateTime lastTapUsageStart;

    protected Dispenser() {}

    public Dispenser(double flowVolume) {
        this.flowVolume = flowVolume;
        this.isOpen = false;
    }

    public void incrementUsageCount() {
        this.usageCount++;
    }

    public void addUsageTime(long seconds) {
        this.totalUsageTimeInSeconds += seconds;
    }
}
