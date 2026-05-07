package com.example.rackapp.controller;

import com.example.rackapp.dto.request.PowerReadingRequest;
import com.example.rackapp.dto.response.RackReportResponse;
import com.example.rackapp.mapper.DtoMapper;
import com.example.rackapp.exception.BadRequestException;
import com.example.rackapp.model.RackReport;
import com.example.rackapp.service.RackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class RackController {

    private final RackService rackService;

    public RackController(RackService rackService) {
        this.rackService = rackService;
    }

    @PostMapping("/readings")
    public ResponseEntity<Void> ingestReading(@RequestBody PowerReadingRequest request) {
        if (request == null || request.getRackId() == null || request.getTimestamp() == null || request.getPowerKw() == null) {
            throw new BadRequestException("Missing required power reading fields");
        }

        rackService.saveReading(DtoMapper.toEntity(request));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/racks/{rackId}/report")
    public ResponseEntity<RackReportResponse> getRackReport(@PathVariable String rackId) {
        RackReport report = rackService.getReport(rackId);
        return ResponseEntity.ok(DtoMapper.toResponse(report));
    }
}
