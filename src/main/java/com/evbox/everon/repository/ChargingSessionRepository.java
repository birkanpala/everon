package com.evbox.everon.repository;

import com.evbox.everon.model.ChargingSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChargingSessionRepository {

    ChargingSession save(final ChargingSession chargingSession);

    Optional<ChargingSession> findById(final UUID id);

    List<ChargingSession> findAll();

}
