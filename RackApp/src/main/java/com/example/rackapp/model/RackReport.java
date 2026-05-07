package com.example.test1.model;

import java.util.List;

public class RackReport {
    private final List<Double> topSpikes;
    private final double recentTrend;

    public RackReport(List<Double> topSpikes, double recentTrend) {
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
