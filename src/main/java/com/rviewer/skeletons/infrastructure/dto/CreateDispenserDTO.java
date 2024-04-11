package com.rviewer.skeletons.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDispenserDTO {
    @JsonProperty("flow_volume")
    private double flowVolume;
}
