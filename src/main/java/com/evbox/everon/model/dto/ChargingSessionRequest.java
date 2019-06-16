package com.evbox.everon.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ChargingSessionRequest {

    @NotNull
    @NotBlank
    private String stationId;
}
