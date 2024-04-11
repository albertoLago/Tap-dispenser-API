package com.rviewer.skeletons.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class DispenserTapUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @PositiveOrZero(message = "Volume must be positive or zero")
    @Column(name = "volume")
    private double volume;
    @Column(name = "flow_volume")
    private double flowVolume;
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    @Column(name = "dispenser_id")
    private long dispenserID;

    public DispenserTapUsage() {}

    public DispenserTapUsage(long dispenserID, double flowVolume, LocalDateTime startedAt, LocalDateTime endedAt) {
        this.dispenserID = dispenserID;
        this.flowVolume = flowVolume;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }
}
