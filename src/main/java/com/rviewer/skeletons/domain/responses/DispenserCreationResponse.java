package com.rviewer.skeletons.domain.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DispenserCreationResponse {
    private String id;

    @JsonProperty("flow_volume")
    private double flowVolume;
}
