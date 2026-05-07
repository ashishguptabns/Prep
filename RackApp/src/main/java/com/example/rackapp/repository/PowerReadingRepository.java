package com.example.rackapp.repository;

import com.example.rackapp.entity.PowerReadingEntity;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PowerReadingRepository {

    private final JdbcTemplate jdbcTemplate;

    public PowerReadingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(PowerReadingEntity reading) {
        jdbcTemplate.update(
                "INSERT INTO power_readings (rack_id, timestamp, power_kw) VALUES (?, ?, ?)",
                reading.getRackId(),
                Timestamp.from(reading.getTimestamp()),
                reading.getPowerKw()
        );
    }

    public List<Double> findTopSpikes(String rackId, Instant since) {
        return jdbcTemplate.query(
                "SELECT power_kw FROM power_readings WHERE rack_id = ? AND timestamp >= ? ORDER BY power_kw DESC LIMIT 5",
                new Object[]{rackId, Timestamp.from(since)},
                (rs, rowNum) -> rs.getDouble("power_kw")
        );
    }

    public Double findAverage(String rackId, Instant since) {
        return jdbcTemplate.queryForObject(
                "SELECT AVG(power_kw) FROM power_readings WHERE rack_id = ? AND timestamp >= ?",
                new Object[]{rackId, Timestamp.from(since)},
                Double.class
        );
    }
}
