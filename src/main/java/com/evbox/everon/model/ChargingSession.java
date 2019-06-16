package com.evbox.everon.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ChargingSession {

    private UUID id;

    private String stationId;

    private LocalDateTime startedAt;

    private LocalDateTime stoppedAt;

    private LocalDateTime updatedAt;

    private StatusEnum status;
}
