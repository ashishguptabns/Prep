package com.example.test1.controller;

import com.example.test1.model.PowerReading;
import com.example.test1.model.RackReport;
import com.example.test1.service.RackService;
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
    public ResponseEntity<Void> ingestReading(@RequestBody PowerReading reading) {
        if (reading == null || reading.getRackId() == null || reading.getTimestamp() == null || reading.getPowerKw() == null) {
            return ResponseEntity.badRequest().build();
        }

        rackService.saveReading(reading);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/racks/{rackId}/report")
    public ResponseEntity<RackReport> getRackReport(@PathVariable String rackId) {
        return ResponseEntity.ok(rackService.getReport(rackId));
    }
}
