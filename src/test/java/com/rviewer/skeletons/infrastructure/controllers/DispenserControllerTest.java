package com.rviewer.skeletons.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rviewer.skeletons.domain.entities.Dispenser;
import com.rviewer.skeletons.domain.responses.DispenserCreationResponse;
import com.rviewer.skeletons.domain.responses.DispenserSpendingResponse;
import com.rviewer.skeletons.domain.services.DispenserService;
import com.rviewer.skeletons.domain.services.FinancialCalculationService;
import com.rviewer.skeletons.infrastructure.dto.CreateDispenserDTO;
import com.rviewer.skeletons.infrastructure.dto.StatusUpdateDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@WebMvcTest(DispenserController.class)
public class DispenserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DispenserService dispenserService;

    @MockBean
    private FinancialCalculationService financialCalculationService;

    @Autowired
    private ObjectMapper objectMapper;

    // Test creating a dispenser returns OK status
    @Test
    void createDispenser_ReturnsOk() throws Exception {
        double flowVolume = 0.0653;
        Dispenser mockDispenser = new Dispenser(flowVolume);
        DispenserCreationResponse expectedResponse = new DispenserCreationResponse("1", flowVolume);

        given(dispenserService.createDispenser(any(Double.class))).willReturn(mockDispenser);

        mockMvc.perform(post("/dispenser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateDispenserDTO(flowVolume))))
                .andExpect(status().isOk());
    }

    // Test updating a dispenser's status returns Accepted status
    @Test
    void updateDispenserStatus_ReturnsAccepted() throws Exception {
        Long dispenserId = 1L;
        StatusUpdateDTO statusUpdateDTO = new StatusUpdateDTO("open", null);

        mockMvc.perform(put("/dispenser/{id}/status", dispenserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdateDTO)))
                .andExpect(status().isAccepted());
    }

    // Test retrieving a dispenser's spending returns the correct response
    @Test
    void getDispenserSpending_ReturnsSpendingResponse() throws Exception {
        long dispenserId = 1L;
        DispenserSpendingResponse spendingResponse = new DispenserSpendingResponse(100.0, null);
        given(financialCalculationService.getDispenserSpending(dispenserId)).willReturn(spendingResponse);

        mockMvc.perform(get("/dispenser/{id}/spending", dispenserId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.0));
    }
}
