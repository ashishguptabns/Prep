package com.example.rackapp.service;

import com.example.rackapp.entity.PowerReadingEntity;
import com.example.rackapp.entity.RackEntity;
import com.example.rackapp.model.RackReport;
import com.example.rackapp.repository.PowerReadingRepository;
import com.example.rackapp.repository.RackRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RackService {

    private final PowerReadingRepository powerReadingRepository;
    private final RackRepository rackRepository;

    public RackService(PowerReadingRepository powerReadingRepository, RackRepository rackRepository) {
        this.powerReadingRepository = powerReadingRepository;
        this.rackRepository = rackRepository;
    }

    @Transactional
    public void saveReading(PowerReadingEntity reading) {
        rackRepository.findById(reading.getRackId())
                .orElseGet(() -> {
                    RackEntity rack = new RackEntity(reading.getRackId(), "unknown", 0.0);
                    rackRepository.save(rack);
                    return rack;
                });
        powerReadingRepository.save(reading);
    }

    @Transactional(readOnly = true)
    public RackReport getReport(String rackId) {
        Instant now = Instant.now();
        Instant days30Ago = now.minus(30, ChronoUnit.DAYS);
        Instant days7Ago = now.minus(7, ChronoUnit.DAYS);

        return powerReadingRepository.findReport(rackId, days30Ago, days7Ago);
    }
}
