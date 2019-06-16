package com.evbox.everon.controller;

import com.evbox.everon.model.dto.ChargingSessionRequest;
import com.evbox.everon.model.dto.ChargingSessionResponse;
import com.evbox.everon.model.dto.ChargingSessionsSummaryResponse;
import com.evbox.everon.service.ChargingSessionService;
import com.evbox.everon.service.ChargingSessionStatisticsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@AllArgsConstructor
public class ChargingSessionController {

    private final ChargingSessionService chargingSessionService;
    private final ChargingSessionStatisticsService chargingSessionStatisticsService;

    @PostMapping("/chargingSessions")
    public ResponseEntity<ChargingSessionResponse> createChargingSession(@RequestBody @Valid ChargingSessionRequest request) {

        final ChargingSessionResponse session = chargingSessionService.create(request.getStationId());

        return ok().body(session);
    }

    @PutMapping("/chargingSessions/{id}")
    public ResponseEntity<ChargingSessionResponse> stopChargingSession(@PathVariable String id) {

        final ChargingSessionResponse session = chargingSessionService.stop(id);

        return ok().body(session);
    }

    @GetMapping("/chargingSessions")
    public ResponseEntity<List<ChargingSessionResponse>> getAllChargingSessions() {

        final List<ChargingSessionResponse> sessions = chargingSessionService.getAll();

        return ok().body(sessions);
    }

    @GetMapping("/chargingSessions/summary")
    public ResponseEntity<ChargingSessionsSummaryResponse> getSummary() {

        final ChargingSessionsSummaryResponse summary = chargingSessionStatisticsService.getSummary();

        return ok().body(summary);
    }
}
