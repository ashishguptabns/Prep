package com.example.rackapp.mapper;

import com.example.rackapp.dto.request.PowerReadingRequest;
import com.example.rackapp.dto.response.RackReportResponse;
import com.example.rackapp.entity.PowerReadingEntity;
import com.example.rackapp.model.RackReport;

public final class DtoMapper {

    private DtoMapper() {
    }

    public static PowerReadingEntity toEntity(PowerReadingRequest request) {
        return new PowerReadingEntity(request.getRackId(), request.getTimestamp(), request.getPowerKw());
    }

    public static RackReportResponse toResponse(RackReport report) {
        return new RackReportResponse(report.getTopSpikes(), report.getRecentTrend());
    }
}
