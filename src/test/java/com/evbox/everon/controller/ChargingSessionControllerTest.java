package com.evbox.everon.controller;

import com.evbox.everon.model.StatusEnum;
import com.evbox.everon.model.dto.ChargingSessionRequest;
import com.evbox.everon.model.dto.ChargingSessionResponse;
import com.evbox.everon.model.dto.ChargingSessionsSummaryResponse;
import com.evbox.everon.service.ChargingSessionService;
import com.evbox.everon.service.ChargingSessionStatisticsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.evbox.everon.DateTimeUtils.format;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ChargingSessionControllerTest {

    @MockBean
    private ChargingSessionService chargingSessionService;

    @MockBean
    private ChargingSessionStatisticsService chargingSessionStatisticsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String STATION_ID = "EV-1234";

    @Test
    @DisplayName("POST /chargingSessions - 200_OK")
    void shouldCreateChargingSession() throws Exception {

        //given
        ChargingSessionRequest request = new ChargingSessionRequest(STATION_ID);

        ChargingSessionResponse expectedResponse = createSessionResponse(StatusEnum.IN_PROGRESS);

        Mockito.doReturn(expectedResponse).when(chargingSessionService).create(anyString());

        //when
        mockMvc.perform(post("/chargingSessions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(valueAsString(request)))
        //then
                            .andExpect(status().isOk())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

                            .andExpect(jsonPath("$.id", equalTo(expectedResponse.getId().toString())))
                            .andExpect(jsonPath("$.stationId", equalTo(expectedResponse.getStationId())))
                            .andExpect(jsonPath("$.updatedAt", equalTo(format(expectedResponse.getUpdatedAt()))))
                            .andExpect(jsonPath("$.status", equalTo(expectedResponse.getStatus().toString())));
    }

    @Test
    @DisplayName("POST /chargingSessions - 400_Bad_Request")
    void shouldReturnBadRequestIfStationIdIsNull() throws Exception {

        //given
        ChargingSessionRequest request = new ChargingSessionRequest(null);

        //when
        mockMvc.perform(post("/chargingSessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString(request)))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /chargingSessions/{id} - 200_OK")
    void shouldStopChargingSessionById() throws Exception {

        //given
        ChargingSessionResponse expectedResponse = createSessionResponse(StatusEnum.FINISHED);

        Mockito.doReturn(expectedResponse).when(chargingSessionService).stop(anyString());

        //when
        mockMvc.perform(put("/chargingSessions/{id}", expectedResponse.getId()))
                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

                .andExpect(jsonPath("$.id", equalTo(expectedResponse.getId().toString())))
                .andExpect(jsonPath("$.stationId", equalTo(expectedResponse.getStationId())))
                .andExpect(jsonPath("$.updatedAt", equalTo(format(expectedResponse.getUpdatedAt()))))
                .andExpect(jsonPath("$.status", equalTo(expectedResponse.getStatus().toString())));
    }

    @Test
    @DisplayName("GET /chargingSessions - 200_OK Empty")
    void shouldGetEmptyArrayIfNoChargingSessions() throws Exception {

        //given
        Mockito.doReturn(emptyList()).when(chargingSessionService).getAll();

        //when
        mockMvc.perform(get("/chargingSessions"))
                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

                .andExpect(jsonPath("$", empty()));
    }

    @Test
    @DisplayName("GET /chargingSessions - 200_OK One Session")
    void shouldGetOneIfThereIsOneChargingSession() throws Exception {

        //given
        ChargingSessionResponse session = createSessionResponse(StatusEnum.FINISHED);

        Mockito.doReturn(asList(session)).when(chargingSessionService).getAll();

        //when
        mockMvc.perform(get("/chargingSessions"))
                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", equalTo(session.getId().toString())))
                .andExpect(jsonPath("$[0].stationId", equalTo(session.getStationId())))
                .andExpect(jsonPath("$[0].updatedAt", equalTo(format(session.getUpdatedAt()))))
                .andExpect(jsonPath("$[0].status", equalTo(session.getStatus().toString())));
    }

    @Test
    @DisplayName("GET /chargingSessions - 200_OK Multiple Sessions")
    void shouldGetMultipleIfThereAreMultipleChargingSessions() throws Exception {

        //given
        ChargingSessionResponse session1 = createSessionResponse(StatusEnum.FINISHED);
        ChargingSessionResponse session2 = createSessionResponse(StatusEnum.IN_PROGRESS);

        Mockito.doReturn(asList(session1, session2)).when(chargingSessionService).getAll();

        //when
        mockMvc.perform(get("/chargingSessions"))
                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", equalTo(session1.getId().toString())))
                .andExpect(jsonPath("$[0].stationId", equalTo(session1.getStationId())))
                .andExpect(jsonPath("$[0].updatedAt", equalTo(format(session1.getUpdatedAt()))))
                .andExpect(jsonPath("$[0].status", equalTo(session1.getStatus().toString())))
                .andExpect(jsonPath("$[1].id", equalTo(session2.getId().toString())))
                .andExpect(jsonPath("$[1].stationId", equalTo(session2.getStationId())))
                .andExpect(jsonPath("$[1].updatedAt", equalTo(format(session2.getUpdatedAt()))))
                .andExpect(jsonPath("$[1].status", equalTo(session2.getStatus().toString())));
    }

    @Test
    @DisplayName("GET /chargingSessions/summary - 200_OK")
    void shouldGetSummary() throws Exception {

        //given
        ChargingSessionsSummaryResponse summary = new ChargingSessionsSummaryResponse(2,3);

        Mockito.doReturn(summary).when(chargingSessionStatisticsService).getSummary();

        //when
        mockMvc.perform(get("/chargingSessions/summary"))
                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

                .andExpect(jsonPath("$.startedCount", equalTo(2)))
                .andExpect(jsonPath("$.stoppedCount", equalTo(3)))
                .andExpect(jsonPath("$.totalCount", equalTo(5)));
    }

    private ChargingSessionResponse createSessionResponse(StatusEnum status) {
        return new ChargingSessionResponse(UUID.randomUUID(), STATION_ID,  now(), status);
    }

    private String valueAsString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
