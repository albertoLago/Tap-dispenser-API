package com.rviewer.skeletons.domain.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DispenserSpendingResponse {

    private double amount;
    private List<UsageLine> usages;


    // Inner class to represent usage lines
    @Getter
    @Setter
    @AllArgsConstructor
    public static class UsageLine {
        private String openedAt;
        private String closedAt;
        private double flowVolume;
        private double totalSpent;
    }
}
