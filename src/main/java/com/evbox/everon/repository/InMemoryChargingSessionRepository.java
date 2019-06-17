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

    /**
     * Charging sessions are stored in ConcurrentHashMap for thread-safe access and
     * constant time operations.
     */
    public InMemoryChargingSessionRepository() {
        chargingSessions = new ConcurrentHashMap<>();
    }

    /**
     * Puts charging session to map.
     *
     * @param chargingSession Charging session
     * @return Persisted charging session
     */
    @Override
    public ChargingSession save(final ChargingSession chargingSession) {

        Objects.requireNonNull(chargingSession);

        chargingSessions.put(chargingSession.getId(), chargingSession);

        return chargingSession;
    }

    /**
     * Gets charging session by ID
     *
     * @param id ID of the charging session
     * @return Charging session
     */
    @Override
    public Optional<ChargingSession> findById(final UUID id) {

        Objects.requireNonNull(id);

        return Optional.ofNullable(chargingSessions.get(id));
    }

    /**
     * Returns all the charging sessions
     *
     * @return List of charging sessions
     */
    @Override
    public List<ChargingSession> findAll() {

        return new ArrayList<>(chargingSessions.values());
    }
}
