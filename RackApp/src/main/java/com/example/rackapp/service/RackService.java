package com.example.rackapp.service;

import com.example.rackapp.model.PowerReading;
import com.example.rackapp.model.RackReport;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class RackService {

    private final JdbcTemplate jdbcTemplate;

    public RackService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveReading(PowerReading reading) {
        jdbcTemplate.update(
                "INSERT INTO power_readings (rack_id, timestamp, power_kw) VALUES (?, ?, ?)",
                reading.getRackId(),
                Timestamp.from(reading.getTimestamp()),
                reading.getPowerKw()
        );
    }

    public RackReport getReport(String rackId) {
        Instant now = Instant.now();
        Timestamp days30Ago = Timestamp.from(now.minus(30, ChronoUnit.DAYS));
        Timestamp days7Ago = Timestamp.from(now.minus(7, ChronoUnit.DAYS));

        List<Double> topSpikes = jdbcTemplate.query(
                "SELECT power_kw FROM power_readings WHERE rack_id = ? AND timestamp >= ? ORDER BY power_kw DESC LIMIT 5",
                new Object[]{rackId, days30Ago},
                (rs, rowNum) -> rs.getDouble("power_kw")
        );

        Double average = jdbcTemplate.queryForObject(
                "SELECT AVG(power_kw) FROM power_readings WHERE rack_id = ? AND timestamp >= ?",
                new Object[]{rackId, days7Ago},
                Double.class
        );

        return new RackReport(topSpikes, average == null ? 0.0 : average);
    }
}
