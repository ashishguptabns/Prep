package com.example.rackapp.repository;

import com.example.rackapp.entity.RackEntity;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RackRepository {

    private final JdbcTemplate jdbcTemplate;

    public RackRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<RackEntity> findById(String rackId) {
        return jdbcTemplate.query(
                "SELECT rack_id, site_id, max_power_kw FROM racks WHERE rack_id = ?",
                new Object[]{rackId},
                rs -> rs.next() ?
                        Optional.of(new RackEntity(
                                rs.getString("rack_id"),
                                rs.getString("site_id"),
                                rs.getDouble("max_power_kw")
                        )) : Optional.empty()
        );
    }
}
