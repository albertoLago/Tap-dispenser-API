package com.rviewer.skeletons;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rviewer.skeletons.infrastructure.dto.CreateDispenserDTO;
import com.rviewer.skeletons.infrastructure.dto.StatusUpdateDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FullWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Test the entire lifecycle
    @Test
    public void testDispenserLifecycle() throws Exception {
        CreateDispenserDTO createDispenserDTO = new CreateDispenserDTO(1.356);
        String dispenserJson = objectMapper.writeValueAsString(createDispenserDTO);

        // Perform the creation and extract the dispenser ID from the response
        String createResponseString = mockMvc.perform(post("/dispenser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dispenserJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode createResponseJson = objectMapper.readTree(createResponseString);
        Long dispenserId = createResponseJson.get("id").asLong();

        // Update the status of the dispenser to "open" at a specified update time
        LocalDateTime updatedTime = LocalDateTime.now();
        StatusUpdateDTO statusUpdateDTO = new StatusUpdateDTO("open", updatedTime);
        mockMvc.perform(put("/dispenser/{id}/status", dispenserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdateDTO)))
                .andExpect(status().isAccepted());

        // Request the spending data for the dispenser and validate the response
        mockMvc.perform(get("/dispenser/{id}/spending", dispenserId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amount").exists());
    }
}
