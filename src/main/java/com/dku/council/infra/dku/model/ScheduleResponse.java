package com.dku.council.infra.dku.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScheduleResponse {
    private final String msg;
    private final String data;
    private final boolean success;
}
