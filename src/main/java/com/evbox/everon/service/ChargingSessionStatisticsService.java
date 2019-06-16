package com.evbox.everon.service;

import com.evbox.everon.model.ChargingSession;
import com.evbox.everon.model.StatusEnum;
import com.evbox.everon.model.dto.ChargingSessionsSummaryResponse;
import com.evbox.everon.repository.ChargingSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChargingSessionStatisticsService {

    private final ChargingSessionRepository repository;

    public ChargingSessionsSummaryResponse getSummary() {

        final List<ChargingSession> all = repository.findAll();

        final LocalDateTime from = LocalDateTime.now().minusMinutes(1);

        final List<ChargingSession> sessions = all.stream()
                .filter(s -> !s.getUpdatedAt().isBefore(from))
                .collect(Collectors.toList());

        int inProgressCount = (int) sessions.stream().filter(s-> s.getStatus() == StatusEnum.IN_PROGRESS).count();

        return new ChargingSessionsSummaryResponse(inProgressCount, sessions.size()-inProgressCount);
    }


//    @Scheduled(fixedDelay = 1000)
//    private void schedule() {
//        log.info(Instant.now().toString());
//    }

}
