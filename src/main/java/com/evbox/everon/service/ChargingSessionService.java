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

    public ChargingSessionResponse create(final String stationId) {

        Objects.requireNonNull(stationId);

        final ChargingSession chargingSession = repository.save(createSession(stationId));

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

    public ChargingSessionResponse stop(final String id) {

        Objects.requireNonNull(id);

        final UUID uuid = UUID.fromString(id);

        final ChargingSession chargingSession = repository.findById(uuid)
                .filter(s -> s.getStatus() == StatusEnum.IN_PROGRESS)
                .orElseThrow(() -> new ResourceNotFoundException("No active session found with id: " + id));

        finishSession(chargingSession);

        repository.save(chargingSession);

        return ChargingSessionResponse.from(chargingSession);
    }

    private void finishSession(final ChargingSession chargingSession) {

        final LocalDateTime stoppedAt = LocalDateTime.now();

        chargingSession.setStatus(StatusEnum.FINISHED);
        chargingSession.setUpdatedAt(stoppedAt);
        chargingSession.setStoppedAt(stoppedAt);
    }

    public List<ChargingSessionResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(ChargingSessionResponse::from)
                .collect(Collectors.toList());
    }

}
