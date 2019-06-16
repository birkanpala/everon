package com.evbox.everon.repository;

import com.evbox.everon.model.ChargingSession;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryChargingSessionRepository implements ChargingSessionRepository {

    private final Map<UUID, ChargingSession> chargingSessions;

    public InMemoryChargingSessionRepository() {
        chargingSessions = new ConcurrentHashMap<>();
    }

    @Override
    public ChargingSession save(final ChargingSession chargingSession) {

        Objects.requireNonNull(chargingSession);

        chargingSessions.put(chargingSession.getId(), chargingSession);

        return chargingSession;
    }

    @Override
    public Optional<ChargingSession> findById(final UUID id) {

        Objects.requireNonNull(id);

        return Optional.ofNullable(chargingSessions.get(id));
    }

    @Override
    public List<ChargingSession> findAll() {

        return new ArrayList<>(chargingSessions.values());
    }
}
