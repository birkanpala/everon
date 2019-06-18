package com.evbox.everon.integration;

import com.evbox.everon.model.StatusEnum;
import com.evbox.everon.model.dto.ChargingSessionRequest;
import com.evbox.everon.model.dto.ChargingSessionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("int-test")
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ChargingSessionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String STATION_ID = "EV-1234";

    @Test
    @DisplayName("Charging session should be started and appear in summary")
    void shouldCreateChargingSessionAndViewInSummary() throws Exception {

        ChargingSessionRequest request = new ChargingSessionRequest(STATION_ID);

        //start a new session
        mockMvc.perform(post("/chargingSessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

                .andExpect(jsonPath("$.stationId", equalTo(STATION_ID)))
                .andExpect(jsonPath("$.status", equalTo(StatusEnum.IN_PROGRESS.toString())));

        //should appear in summary
        mockMvc.perform(get("/chargingSessions/summary"))
                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

                .andExpect(jsonPath("$.startedCount", equalTo(1)))
                .andExpect(jsonPath("$.stoppedCount", equalTo(0)))
                .andExpect(jsonPath("$.totalCount", equalTo(1)));
    }

    @Test
    @DisplayName("Charging session should be started, stopped and appear in summary")
    void shouldCreateAndStopChargingSessionAndViewInSummary() throws Exception {

        ChargingSessionRequest request = new ChargingSessionRequest(STATION_ID);

        //start a new session
        MvcResult mvcResult = mockMvc.perform(post("/chargingSessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

                .andExpect(jsonPath("$.stationId", equalTo(STATION_ID)))
                .andExpect(jsonPath("$.status", equalTo(StatusEnum.IN_PROGRESS.toString())))
                .andReturn();

        ChargingSessionResponse chargingSession = read(mvcResult, ChargingSessionResponse.class);

        //stop the session
        mockMvc.perform(put("/chargingSessions/{id}", chargingSession.getId()))
                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

                .andExpect(jsonPath("$.id", equalTo(chargingSession.getId().toString())))
                .andExpect(jsonPath("$.stationId", equalTo(chargingSession.getStationId())))
                .andExpect(jsonPath("$.status", equalTo(StatusEnum.FINISHED.toString())));

        //should appear in summary
        mockMvc.perform(get("/chargingSessions/summary"))
                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

                .andExpect(jsonPath("$.startedCount", equalTo(1)))
                .andExpect(jsonPath("$.stoppedCount", equalTo(1)))
                .andExpect(jsonPath("$.totalCount", equalTo(2)));
    }


    private <T> T read(MvcResult startResponse, Class<T> theClass) throws UnsupportedEncodingException {
        return deserialize(startResponse.getResponse().getContentAsString(), theClass);
    }

    private <T> T deserialize(String json, Class<T> theClass) {
        try {
            return objectMapper.readValue(json, theClass);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize json object!", e);
        }
    }
}
