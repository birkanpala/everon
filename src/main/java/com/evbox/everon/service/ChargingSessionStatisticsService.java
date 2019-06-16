package com.evbox.everon.service;

import com.evbox.everon.model.dto.ChargingSessionsSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChargingSessionStatisticsService {

    private final Queue<LocalDateTime> startedQueue;
    private final Queue<LocalDateTime> stoppedQueue;

    private static final Duration ONE_MINUTE = Duration.ofMinutes(1L);

    public ChargingSessionStatisticsService() {
        startedQueue = new LinkedBlockingQueue<>();
        stoppedQueue = new LinkedBlockingQueue<>();
    }

    void started(LocalDateTime localDateTime) {

        log.debug("Offering started {}", localDateTime);

        startedQueue.offer(localDateTime);
    }

    void stopped(LocalDateTime localDateTime) {

        log.debug("Offering stopped {}", localDateTime);

        stoppedQueue.offer(localDateTime);
    }


    public ChargingSessionsSummaryResponse getSummary() {

        return new ChargingSessionsSummaryResponse(startedQueue.size(), stoppedQueue.size());
    }


    @Scheduled(fixedDelayString = "${statistics.task.delay.milis:1000}")
    private void schedule() {

        LocalDateTime aMinuteAgo = LocalDateTime.now().minus(ONE_MINUTE);

        while (!startedQueue.isEmpty() && startedQueue.peek().isBefore(aMinuteAgo)) {
            log.debug("Removing started {}", startedQueue.peek());
            startedQueue.remove();
        }

        while (!stoppedQueue.isEmpty() && stoppedQueue.peek().isBefore(aMinuteAgo)) {
            log.debug("Removing stopped {}", stoppedQueue.peek());
            stoppedQueue.remove();
        }
    }
}
