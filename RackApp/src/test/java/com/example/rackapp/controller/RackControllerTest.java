package com.example.rackapp.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void ingestReadingAndReturnOk() throws Exception {
        String payload = "{\"rackId\":\"rack-1\",\"timestamp\":\"2026-05-01T12:00:00Z\",\"powerKw\": 14.5}";

        mockMvc.perform(post("/v1/readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk());
    }

    @Test
    void reportReturnsEmptyTrendWhenNoReadings() throws Exception {
        mockMvc.perform(get("/v1/racks/unknown-rack/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topSpikes").isArray())
                .andExpect(jsonPath("$.topSpikes").isEmpty())
                .andExpect(jsonPath("$.recentTrend").value(0.0));
    }

    @Test
    void reportCalculatesTopSpikesAndAverage() throws Exception {
        String rackId = "rack-2";
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        ingestReading(rackId, now.minus(1, ChronoUnit.DAYS), 20.0);
        ingestReading(rackId, now.minus(2, ChronoUnit.DAYS), 30.0);
        ingestReading(rackId, now.minus(31, ChronoUnit.DAYS), 10.0);

        mockMvc.perform(get("/v1/racks/rack-2/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topSpikes[0]").value(30.0))
                .andExpect(jsonPath("$.topSpikes[1]").value(20.0))
                .andExpect(jsonPath("$.recentTrend").value(25.0));
    }

    @Test
    void reportUsesSingleQuerySemanticsAcrossManyReadings() throws Exception {
        String rackId = "rack-bulk-report";
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        ingestReading(rackId, now.minus(1, ChronoUnit.DAYS), 80.0);
        ingestReading(rackId, now.minus(2, ChronoUnit.DAYS), 70.0);
        ingestReading(rackId, now.minus(3, ChronoUnit.DAYS), 60.0);
        ingestReading(rackId, now.minus(4, ChronoUnit.DAYS), 50.0);
        ingestReading(rackId, now.minus(5, ChronoUnit.DAYS), 40.0);
        ingestReading(rackId, now.minus(6, ChronoUnit.DAYS), 30.0);
        ingestReading(rackId, now.minus(6, ChronoUnit.DAYS).minus(12, ChronoUnit.HOURS), 20.0);
        ingestReading(rackId, now.minus(6, ChronoUnit.DAYS).minus(23, ChronoUnit.HOURS), 10.0);

        ingestReading(rackId, now.minus(8, ChronoUnit.DAYS), 100.0);
        ingestReading(rackId, now.minus(9, ChronoUnit.DAYS), 90.0);
        ingestReading(rackId, now.minus(10, ChronoUnit.DAYS), 55.0);
        ingestReading(rackId, now.minus(15, ChronoUnit.DAYS), 150.0);
        ingestReading(rackId, now.minus(20, ChronoUnit.DAYS), 200.0);
        ingestReading(rackId, now.minus(25, ChronoUnit.DAYS), 5.0);
        ingestReading(rackId, now.minus(29, ChronoUnit.DAYS), 65.0);

        ingestReading(rackId, now.minus(31, ChronoUnit.DAYS), 999.0);
        ingestReading(rackId, now.minus(45, ChronoUnit.DAYS), 300.0);
        ingestReading("other-rack-bulk-report", now.minus(1, ChronoUnit.DAYS), 500.0);

        mockMvc.perform(get("/v1/racks/{rackId}/report", rackId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topSpikes.length()").value(5))
                .andExpect(jsonPath("$.topSpikes[0]").value(200.0))
                .andExpect(jsonPath("$.topSpikes[1]").value(150.0))
                .andExpect(jsonPath("$.topSpikes[2]").value(100.0))
                .andExpect(jsonPath("$.topSpikes[3]").value(90.0))
                .andExpect(jsonPath("$.topSpikes[4]").value(80.0))
                .andExpect(jsonPath("$.recentTrend").value(45.0));
    }

    private void ingestReading(String rackId, Instant timestamp, double powerKw) throws Exception {
        String payload = String.format(
                Locale.ROOT,
                "{\"rackId\":\"%s\",\"timestamp\":\"%s\",\"powerKw\": %.1f}",
                rackId,
                timestamp,
                powerKw
        );

        mockMvc.perform(post("/v1/readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk());
    }
}
