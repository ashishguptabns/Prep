package com.example.rackapp.model;

import java.time.Instant;

public class PowerReading {
    private String rackId;
    private Instant timestamp;
    private Double powerKw;

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
