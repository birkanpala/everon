package com.evbox.everon.service;

import com.evbox.everon.model.dto.ChargingSessionsSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChargingSessionStatisticsService {

    private final Queue<LocalDateTime> startedQueue;
    private final Queue<LocalDateTime> stoppedQueue;

    private static final Duration ONE_MINUTE = Duration.ofMinutes(1L);

    /**
     * PriorityBlockingQueue is used as a thread-safe Priority Queue implementation.
     * Offers O(log n) time complexity for both offer and poll operations, O(1) for
     * size.
     */
    public ChargingSessionStatisticsService() {
        startedQueue = new PriorityBlockingQueue<>();
        stoppedQueue = new PriorityBlockingQueue<>();
    }

    /**
     * Offers new started session event to queue.
     *
     * Time complexity is O(log n) by the nature of Priority Queue
     *
     * @param localDateTime start time
     */
    void started(LocalDateTime localDateTime) {

        startedQueue.offer(localDateTime);

        log.info("Offered started event {}", localDateTime);
    }

    /**
     * Offers new stopped session event to queue.
     *
     * Time complexity is O(log n) by the nature of Priority Queue
     *
     * @param localDateTime stopped time
     */
    void stopped(LocalDateTime localDateTime) {

        stoppedQueue.offer(localDateTime);

        log.info("Offered stopped event {}", localDateTime);
    }

    /**
     * Retrieves statistics summary for the last minute.
     * Time complexity is O(1).
     *
     * @return ChargingSessionsSummaryResponse
     */
    public ChargingSessionsSummaryResponse getSummary() {

        return new ChargingSessionsSummaryResponse(startedQueue.size(), stoppedQueue.size());
    }

    /**
     * Background task that removes expired sessions from priority queue.
     * Earliest session is at the head of the queue.
     *
     */
    @Scheduled(fixedDelayString = "${statistics.task.delay.milis:1000}")
    private void removeExpired() {

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
