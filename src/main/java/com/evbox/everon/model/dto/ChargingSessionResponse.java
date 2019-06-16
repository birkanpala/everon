package com.evbox.everon.model.dto;

import com.evbox.everon.model.ChargingSession;
import com.evbox.everon.model.StatusEnum;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class ChargingSessionResponse {

    private UUID id;

    private String stationId;

    private LocalDateTime updatedAt;

    private StatusEnum status;

    public static ChargingSessionResponse from(final ChargingSession chargingSession) {
        return new ChargingSessionResponse(chargingSession.getId(),
                chargingSession.getStationId(),
                chargingSession.getUpdatedAt(),
                chargingSession.getStatus());
    }
}
