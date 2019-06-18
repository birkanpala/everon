package com.evbox.everon.service;

import com.evbox.everon.model.dto.ChargingSessionsSummaryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ChargingSessionStatisticsServiceTest {

    private ChargingSessionStatisticsService chargingSessionStatisticsService;


    @BeforeEach
    public void init() {
        chargingSessionStatisticsService = new ChargingSessionStatisticsService();
    }

    @Test
    @DisplayName("Start event should appear in summary")
    public void shouldStoreStartEvent() {

        //given
        chargingSessionStatisticsService.started(now());

        //when
        ChargingSessionsSummaryResponse summary = chargingSessionStatisticsService.getSummary();

        //then
        assertThat(summary, is(notNullValue()));
        assertThat(summary.getStartedCount(), equalTo(1));
        assertThat(summary.getStoppedCount(), equalTo(0));
        assertThat(summary.getTotalCount(), equalTo(1));
    }

    @Test
    @DisplayName("Stop event should appear in summary")
    public void shouldStoreStopEvent() {

        //given
        chargingSessionStatisticsService.stopped(now());

        //when
        ChargingSessionsSummaryResponse summary = chargingSessionStatisticsService.getSummary();

        //then
        assertThat(summary, is(notNullValue()));
        assertThat(summary.getStartedCount(), equalTo(0));
        assertThat(summary.getStoppedCount(), equalTo(1));
        assertThat(summary.getTotalCount(), equalTo(1));
    }

    @Test
    @DisplayName("Empty statistics should appear in summary")
    public void shouldGetEmptyStatistics() {

        //when
        ChargingSessionsSummaryResponse summary = chargingSessionStatisticsService.getSummary();

        //then
        assertThat(summary, is(notNullValue()));
        assertThat(summary.getStartedCount(), equalTo(0));
        assertThat(summary.getStoppedCount(), equalTo(0));
        assertThat(summary.getTotalCount(), equalTo(0));
    }

    @Test
    @DisplayName("Expired events should be removed from summary")
    public void shouldExpireEvents() {

        //given
        LocalDateTime moreThanOneMinuteAgo = now().minusMinutes(1).minusSeconds(1);
        chargingSessionStatisticsService.stopped(moreThanOneMinuteAgo);
        chargingSessionStatisticsService.started(moreThanOneMinuteAgo);
        chargingSessionStatisticsService.started(now());

        ReflectionTestUtils.invokeMethod(chargingSessionStatisticsService, "removeExpired");

        //when
        ChargingSessionsSummaryResponse summary = chargingSessionStatisticsService.getSummary();

        //then
        assertThat(summary, is(notNullValue()));
        assertThat(summary.getStartedCount(), equalTo(1));
        assertThat(summary.getStoppedCount(), equalTo(0));
        assertThat(summary.getTotalCount(), equalTo(1));
    }

    @Test
    @DisplayName("New events should not be removed from summary")
    public void shouldNotExpireNewEvents() {

        //given
        chargingSessionStatisticsService.started(now());

        ReflectionTestUtils.invokeMethod(chargingSessionStatisticsService, "removeExpired");

        //when
        ChargingSessionsSummaryResponse summary = chargingSessionStatisticsService.getSummary();

        //then
        assertThat(summary, is(notNullValue()));
        assertThat(summary.getStartedCount(), equalTo(1));
        assertThat(summary.getStoppedCount(), equalTo(0));
        assertThat(summary.getTotalCount(), equalTo(1));
    }
}
