package com.example.rackapp.service;

import com.example.rackapp.entity.PowerReadingEntity;
import com.example.rackapp.model.RackReport;
import com.example.rackapp.repository.PowerReadingRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RackService {

    private final PowerReadingRepository powerReadingRepository;

    public RackService(PowerReadingRepository powerReadingRepository) {
        this.powerReadingRepository = powerReadingRepository;
    }

    @Transactional
    public void saveReading(PowerReadingEntity reading) {
        powerReadingRepository.save(reading);
    }

    @Transactional(readOnly = true)
    public RackReport getReport(String rackId) {
        Instant now = Instant.now();
        Instant days30Ago = now.minus(30, ChronoUnit.DAYS);
        Instant days7Ago = now.minus(7, ChronoUnit.DAYS);

        List<Double> topSpikes = powerReadingRepository.findTopSpikes(rackId, days30Ago);
        Double average = powerReadingRepository.findAverage(rackId, days7Ago);

        return new RackReport(topSpikes, average == null ? 0.0 : average);
    }
}
