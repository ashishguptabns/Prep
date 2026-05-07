package com.example.rackapp.dto;

import com.example.rackapp.dto.request.PowerReadingRequest;
import com.example.rackapp.dto.response.RackReportResponse;
import com.example.rackapp.model.PowerReading;
import com.example.rackapp.model.RackReport;

public final class DtoMapper {

    private DtoMapper() {
    }

    public static PowerReading toDomain(PowerReadingRequest request) {
        PowerReading reading = new PowerReading();
        reading.setRackId(request.getRackId());
        reading.setTimestamp(request.getTimestamp());
        reading.setPowerKw(request.getPowerKw());
        return reading;
    }

    public static RackReportResponse toResponse(RackReport report) {
        return new RackReportResponse(report.getTopSpikes(), report.getRecentTrend());
    }
}
