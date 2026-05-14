package com.example.rackapp.diagnostics;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.diagnostics.FailureAnalysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RackDataSourceFailureAnalyzerTest {

    private final RackDataSourceFailureAnalyzer analyzer = new RackDataSourceFailureAnalyzer();

    @Test
    void analyzesDataSourceBeanCreationFailures() {
        SQLException connectionFailure = new SQLException("Connection refused");
        BeanCreationException failure = new BeanCreationException(
                "dataSource",
                "Failed to create DataSource",
                connectionFailure
        );

        FailureAnalysis analysis = analyzer.analyze(new IllegalStateException("Startup failed", failure));

        assertNotNull(analysis);
        assertEquals(connectionFailure, analysis.getCause());
        assertTrue(analysis.getDescription().contains("RackApp could not create or initialize its database connection."));
        assertTrue(analysis.getDescription().contains("Bean: dataSource"));
        assertTrue(analysis.getAction().contains("spring.datasource.*"));
    }

    @Test
    void ignoresUnrelatedBeanCreationFailures() {
        BeanCreationException failure = new BeanCreationException(
                "rackController",
                "Failed to create controller",
                new IllegalArgumentException("Missing dependency")
        );

        FailureAnalysis analysis = analyzer.analyze(new IllegalStateException("Startup failed", failure));

        assertNull(analysis);
    }
}
