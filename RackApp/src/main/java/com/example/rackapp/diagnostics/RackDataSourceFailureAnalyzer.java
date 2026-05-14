package com.example.rackapp.diagnostics;

import java.sql.SQLException;
import java.util.Set;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalyzer;

public class RackDataSourceFailureAnalyzer implements FailureAnalyzer {

    private static final Set<String> DATA_SOURCE_BEAN_NAMES = Set.of(
            "dataSource",
            "jdbcTemplate",
            "dataSourceScriptDatabaseInitializer"
    );

    @Override
    public FailureAnalysis analyze(Throwable failure) {
        BeanCreationException dataSourceFailure = findDataSourceFailure(failure);
        if (dataSourceFailure == null) {
            return null;
        }

        Throwable rootCause = rootCause(dataSourceFailure);
        String description = "RackApp could not create or initialize its database connection."
                + System.lineSeparator()
                + "Bean: " + dataSourceFailure.getBeanName()
                + System.lineSeparator()
                + "Cause: " + rootCause.getMessage();
        String action = "Check the active Spring profile and the spring.datasource.* properties. "
                + "For local and test runs, use the H2 settings from application-dev.properties or "
                + "application-test.properties. For stage/prod, verify the PostgreSQL URL, credentials, "
                + "driver, and network access before restarting RackApp.";

        return new FailureAnalysis(description, action, rootCause);
    }

    private BeanCreationException findDataSourceFailure(Throwable failure) {
        Throwable current = failure;
        while (current != null) {
            if (current instanceof BeanCreationException beanCreationException
                    && isDataSourceFailure(beanCreationException)) {
                return beanCreationException;
            }
            current = current.getCause();
        }
        return null;
    }

    private boolean isDataSourceFailure(BeanCreationException exception) {
        String beanName = exception.getBeanName();
        return DATA_SOURCE_BEAN_NAMES.contains(beanName) || findCause(exception, SQLException.class) != null;
    }

    private <T extends Throwable> T findCause(Throwable failure, Class<T> causeType) {
        Throwable current = failure;
        while (current != null) {
            if (causeType.isInstance(current)) {
                return causeType.cast(current);
            }
            current = current.getCause();
        }
        return null;
    }

    private Throwable rootCause(Throwable failure) {
        Throwable current = failure;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }
}
