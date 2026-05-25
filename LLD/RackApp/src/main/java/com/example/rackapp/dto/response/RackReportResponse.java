package com.example.rackapp.dto.response;

import java.util.List;

public class RackReportResponse {
    private final List<Double> topSpikes;
    private final double recentTrend;

    public RackReportResponse(List<Double> topSpikes, double recentTrend) {
        this.topSpikes = topSpikes;
        this.recentTrend = recentTrend;
    }

    public List<Double> getTopSpikes() {
        return topSpikes;
    }

    public double getRecentTrend() {
        return recentTrend;
    }
}
