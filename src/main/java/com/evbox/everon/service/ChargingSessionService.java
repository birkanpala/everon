package com.evbox.everon.service;

import com.evbox.everon.errorhandling.ResourceNotFoundException;
import com.evbox.everon.model.ChargingSession;
import com.evbox.everon.model.StatusEnum;
import com.evbox.everon.model.dto.ChargingSessionResponse;
import com.evbox.everon.repository.ChargingSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChargingSessionService {

    private final ChargingSessionRepository repository;
    private final ChargingSessionStatisticsService chargingSessionStatisticsService;

    /**
     * Creates new charging session for the given station id.
     *
     * Operates at O(log n) time complexity due to sending
     * stared event to ChargingSessionStatisticsService.
     *
     * @param stationId Station Id
     * @return Charging session response DTO
     */
    public ChargingSessionResponse create(final String stationId) {

        Objects.requireNonNull(stationId);

        final ChargingSession chargingSession = repository.save(createSession(stationId));

        chargingSessionStatisticsService.started(chargingSession.getStartedAt());

        log.info("New session is created for station {}, with id {}", stationId, chargingSession.getId());

        return ChargingSessionResponse.from(chargingSession);
    }

    private ChargingSession createSession(final String stationId) {

        final LocalDateTime startedAt = LocalDateTime.now();
        final UUID uuid = UUID.randomUUID();

        final ChargingSession chargingSession = new ChargingSession();
        chargingSession.setId(uuid);
        chargingSession.setStationId(stationId);
        chargingSession.setStartedAt(startedAt);
        chargingSession.setUpdatedAt(startedAt);
        chargingSession.setStatus(StatusEnum.IN_PROGRESS);

        return chargingSession;
    }

    /**
     * Stops a charging session by the given session id.
     *
     * Operates at O(log n) time complexity due to sending
     * stopped event to ChargingSessionStatisticsService.
     *
     * @param id Session Id
     * @return Charging session response DTO
     */
    public ChargingSessionResponse stop(final String id) {

        Objects.requireNonNull(id);

        final UUID uuid = UUID.fromString(id);

        final ChargingSession chargingSession = repository.findById(uuid)
                .filter(s -> s.getStatus() == StatusEnum.IN_PROGRESS)
                .orElseThrow(() -> new ResourceNotFoundException("No active session found with id: " + id));

        finishSession(chargingSession);

        repository.save(chargingSession);

        chargingSessionStatisticsService.stopped(chargingSession.getStoppedAt());

        log.info("Session with id {} is stopped.", chargingSession.getId());

        return ChargingSessionResponse.from(chargingSession);
    }

    private void finishSession(final ChargingSession chargingSession) {

        final LocalDateTime stoppedAt = LocalDateTime.now();

        chargingSession.setStatus(StatusEnum.FINISHED);
        chargingSession.setUpdatedAt(stoppedAt);
        chargingSession.setStoppedAt(stoppedAt);
    }

    /**
     * Gets all charging sessions.
     *
     * Operates at O(n) time complexity.
     *
     * @return List of charging sessions
     */
    public List<ChargingSessionResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(ChargingSessionResponse::from)
                .collect(Collectors.toList());
    }

}
