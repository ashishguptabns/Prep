package com.example.rackapp.repository;

import com.example.rackapp.entity.PowerReadingEntity;
import com.example.rackapp.model.RackReport;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
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

    public RackReport findReport(String rackId, Instant topSpikesSince, Instant averageSince) {
        return jdbcTemplate.query(
                """
                SELECT pr.power_kw,
                       (
                           SELECT AVG(avg_reading.power_kw)
                           FROM power_readings avg_reading
                           WHERE avg_reading.rack_id = ? AND avg_reading.timestamp >= ?
                       ) AS recent_trend
                FROM power_readings pr
                WHERE pr.rack_id = ? AND pr.timestamp >= ?
                ORDER BY pr.power_kw DESC
                LIMIT 5
                """,
                new Object[]{
                        rackId,
                        Timestamp.from(averageSince),
                        rackId,
                        Timestamp.from(topSpikesSince)
                },
                rs -> {
                    List<Double> topSpikes = new ArrayList<>();
                    Double recentTrend = null;

                    while (rs.next()) {
                        topSpikes.add(rs.getDouble("power_kw"));
                        if (recentTrend == null) {
                            recentTrend = rs.getDouble("recent_trend");
                            if (rs.wasNull()) {
                                recentTrend = 0.0;
                            }
                        }
                    }

                    return new RackReport(topSpikes, recentTrend == null ? 0.0 : recentTrend);
                }
        );
    }
}
