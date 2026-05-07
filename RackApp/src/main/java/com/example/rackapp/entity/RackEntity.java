package com.example.rackapp.entity;

public class RackEntity {
    private String rackId;
    private String siteId;
    private Double maxPowerKw;

    public RackEntity() {
    }

    public RackEntity(String rackId, String siteId, Double maxPowerKw) {
        this.rackId = rackId;
        this.siteId = siteId;
        this.maxPowerKw = maxPowerKw;
    }

    public String getRackId() {
        return rackId;
    }

    public void setRackId(String rackId) {
        this.rackId = rackId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public Double getMaxPowerKw() {
        return maxPowerKw;
    }

    public void setMaxPowerKw(Double maxPowerKw) {
        this.maxPowerKw = maxPowerKw;
    }
}
