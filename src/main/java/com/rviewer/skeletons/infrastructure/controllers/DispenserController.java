package com.rviewer.skeletons.infrastructure.controllers;

import com.rviewer.skeletons.domain.entities.Dispenser;
import com.rviewer.skeletons.domain.responses.DispenserCreationResponse;
import com.rviewer.skeletons.domain.responses.DispenserSpendingResponse;
import com.rviewer.skeletons.domain.services.DispenserService;
import com.rviewer.skeletons.domain.services.FinancialCalculationService;
import com.rviewer.skeletons.infrastructure.dto.CreateDispenserDTO;
import com.rviewer.skeletons.infrastructure.dto.StatusUpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dispenser")
public class DispenserController {

    private static final Logger log = LoggerFactory.getLogger(DispenserController.class);
    private final DispenserService dispenserService;
    private final FinancialCalculationService financialCalculationService;

    @Autowired
    public DispenserController(DispenserService dispenserService, FinancialCalculationService financialCalculationService) {
        this.dispenserService = dispenserService;
        this.financialCalculationService = financialCalculationService;
    }

    @PostMapping
    public ResponseEntity<DispenserCreationResponse> createDispenser(@RequestBody CreateDispenserDTO createDispenserDTO) {
        Dispenser createdDispenser = dispenserService.createDispenser(createDispenserDTO.getFlowVolume());
        log.info("Dispenser created with ID: {} and flow volume: {}", createdDispenser.getId(), createDispenserDTO.getFlowVolume());
        return new ResponseEntity<>(new DispenserCreationResponse(String.valueOf(createdDispenser.getId()), createdDispenser.getFlowVolume()), HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateDispenserStatus(@PathVariable Long id, @RequestBody StatusUpdateDTO statusUpdateDTO) {
        dispenserService.updateDispenserStatus(id, "open".equals(statusUpdateDTO.getStatus()), statusUpdateDTO.getUpdatedAt());
        log.info("Status '{}' set for dispenser with ID: {}", statusUpdateDTO.getStatus(), id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}/spending")
    public ResponseEntity<DispenserSpendingResponse> getDispenserSpending(@PathVariable Long id) {
        DispenserSpendingResponse spendingResponse = financialCalculationService.getDispenserSpending(id);
        log.info("Spending data requested for dispenser with ID: {}", id);
        return new ResponseEntity<>(spendingResponse, HttpStatus.OK);
    }
}
