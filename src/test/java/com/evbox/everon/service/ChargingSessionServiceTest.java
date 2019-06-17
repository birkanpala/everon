package com.evbox.everon.service;

import com.evbox.everon.errorhandling.ResourceNotFoundException;
import com.evbox.everon.model.ChargingSession;
import com.evbox.everon.model.StatusEnum;
import com.evbox.everon.model.dto.ChargingSessionResponse;
import com.evbox.everon.repository.ChargingSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.evbox.everon.model.dto.ChargingSessionResponse.from;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ChargingSessionServiceTest {

    @InjectMocks
    private ChargingSessionService chargingSessionService;

    @Mock
    private ChargingSessionRepository repository;

    @Mock
    private ChargingSessionStatisticsService statisticsService;

    private static final String STATION_ID = "EV-1234";

    @Test
    @DisplayName("Create should create charging session")
    public void shouldCreateChargingSession() {

        //given
        ChargingSession chargingSession = createSession(StatusEnum.IN_PROGRESS);
        doReturn(chargingSession)
                .when(repository)
                .save(any(ChargingSession.class));

        //when
        ChargingSessionResponse actual = chargingSessionService.create(STATION_ID);

        //then
        assertThat(actual, equalTo(from(chargingSession)));
        verify(statisticsService).started(chargingSession.getStartedAt());
    }

    @Test
    @DisplayName("Create should throw NullPointerException if station id is null")
    public void shouldThrowNullPointerExceptionIfStationIdIsNull() {

        // when, then
        assertThrows(NullPointerException.class,
                () -> chargingSessionService.create(null)
        );
    }

    @Test
    @DisplayName("Stop should stop charging session")
    public void shouldStopChargingSession() {

        //given
        ChargingSession chargingSession = createSession(StatusEnum.IN_PROGRESS);
        doReturn(Optional.of(chargingSession))
                .when(repository)
                .findById(any(UUID.class));

        //when
        String id = chargingSession.getId().toString();
        ChargingSessionResponse actual = chargingSessionService.stop(id);

        //then
        assertThat(actual.getStatus(), equalTo(StatusEnum.FINISHED));
        verify(statisticsService).stopped(actual.getUpdatedAt());
    }

    @Test
    @DisplayName("Stop should throw NullPointerException if id is null")
    public void shouldThrowNullPointerExceptionIfIdIsNull() {

        // when, then
        assertThrows(NullPointerException.class,
                () -> chargingSessionService.stop(null)
        );
    }

    @Test
    @DisplayName("Stop should throw ResourceNotFoundException if entity is not present ")
    public void shouldThrowResourceNotFoundExceptionIfIdIsNotPresent() {

        // when, then
        assertThrows(ResourceNotFoundException.class,
                () -> chargingSessionService.stop(UUID.randomUUID().toString())
        );
    }

    @Test
    @DisplayName("Stop should throw ResourceNotFoundException if session is already finished ")
    public void shouldThrowResourceNotFoundExceptionIfIdIsFinished() {

        //given
        ChargingSession chargingSession = createSession(StatusEnum.FINISHED);
        doReturn(Optional.of(chargingSession))
                .when(repository)
                .findById(any(UUID.class));

        // when, then
        assertThrows(ResourceNotFoundException.class,
                () -> chargingSessionService.stop(chargingSession.getId().toString())
        );
    }

    @Test
    @DisplayName("GetAll should return empty list when repository is empty")
    public void shouldReturnEmptyListIfRepoIsEmpty() {

        //given
        doReturn(emptyList()).when(repository).findAll();

        //when

        List<ChargingSessionResponse> all = chargingSessionService.getAll();

        //then
        assertThat(all, empty());
    }

    @Test
    @DisplayName("GetAll should return one item when repository holds one item")
    public void shouldReturnOneItemListIfRepoHasOneItem() {

        //given
        ChargingSession chargingSession = createSession(StatusEnum.FINISHED);
        doReturn(asList(chargingSession)).when(repository).findAll();

        //when
        List<ChargingSessionResponse> all = chargingSessionService.getAll();

        //then
        assertThat(all, hasSize(1));
        assertThat(all.get(0), equalTo(from(chargingSession)));
    }

    private ChargingSession createSession(StatusEnum status) {

        final LocalDateTime startedAt = now();

        final ChargingSession chargingSession = new ChargingSession();
        chargingSession.setId(UUID.randomUUID());
        chargingSession.setStationId(STATION_ID);
        chargingSession.setStartedAt(startedAt);
        chargingSession.setUpdatedAt(startedAt);
        chargingSession.setStatus(status);

        return chargingSession;
    }
}
