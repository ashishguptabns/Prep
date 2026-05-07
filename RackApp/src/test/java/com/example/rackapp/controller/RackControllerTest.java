package com.example.test1.controller;

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
        String payload1 = "{\"rackId\":\"rack-2\",\"timestamp\":\"2026-05-05T12:00:00Z\",\"powerKw\": 20.0}";
        String payload2 = "{\"rackId\":\"rack-2\",\"timestamp\":\"2026-05-06T12:00:00Z\",\"powerKw\": 30.0}";
        String payload3 = "{\"rackId\":\"rack-2\",\"timestamp\":\"2026-04-10T12:00:00Z\",\"powerKw\": 10.0}";

        mockMvc.perform(post("/v1/readings").contentType(MediaType.APPLICATION_JSON).content(payload1)).andExpect(status().isOk());
        mockMvc.perform(post("/v1/readings").contentType(MediaType.APPLICATION_JSON).content(payload2)).andExpect(status().isOk());
        mockMvc.perform(post("/v1/readings").contentType(MediaType.APPLICATION_JSON).content(payload3)).andExpect(status().isOk());

        mockMvc.perform(get("/v1/racks/rack-2/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topSpikes[0]").value(30.0))
                .andExpect(jsonPath("$.topSpikes[1]").value(20.0))
                .andExpect(jsonPath("$.recentTrend").value(25.0));
    }
}
