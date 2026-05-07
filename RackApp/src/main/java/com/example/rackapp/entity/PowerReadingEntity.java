package com.example.rackapp.entity;

import java.time.Instant;

public class PowerReadingEntity {
    private Long readingId;
    private String rackId;
    private Instant timestamp;
    private Double powerKw;

    public PowerReadingEntity() {
    }

    public PowerReadingEntity(String rackId, Instant timestamp, Double powerKw) {
        this.rackId = rackId;
        this.timestamp = timestamp;
        this.powerKw = powerKw;
    }

    public Long getReadingId() {
        return readingId;
    }

    public void setReadingId(Long readingId) {
        this.readingId = readingId;
    }

    public String getRackId() {
        return rackId;
    }

    public void setRackId(String rackId) {
        this.rackId = rackId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Double getPowerKw() {
        return powerKw;
    }

    public void setPowerKw(Double powerKw) {
        this.powerKw = powerKw;
    }
}
