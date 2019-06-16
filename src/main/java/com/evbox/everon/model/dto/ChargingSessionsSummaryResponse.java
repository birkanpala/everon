package com.evbox.everon.model.dto;

import lombok.Value;

@Value
public class ChargingSessionsSummaryResponse {

    private int totalCount;

    private int startedCount;

    private int stoppedCount;

    public ChargingSessionsSummaryResponse(int startedCount, int stoppedCount) {
        this.startedCount = startedCount;
        this.stoppedCount = stoppedCount;
        this.totalCount = startedCount + stoppedCount;
    }

}
