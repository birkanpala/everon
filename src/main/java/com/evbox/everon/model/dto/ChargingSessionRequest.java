package com.evbox.everon.model.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
public class ChargingSessionRequest {

    @NotNull
    @NotBlank
    private String stationId;
}
