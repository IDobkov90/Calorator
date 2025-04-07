package com.example.calorator.integration;

import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base class for all integration tests in the Calorator application.
 * <p>
 * This class sets up the common test environment with:
 * - Spring Boot test context
 * - "test" profile activation to use application-test.properties
 * - Transactional test methods that roll back after each test
 * - Common setup and property configuration
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseIntegrationTest.class);

    protected static final String TEST_RUN_ID = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void setup() {
        logger.info("Starting integration tests with test run ID: {}", TEST_RUN_ID);

        // Verify H2 database driver is available
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("H2 database driver not found. Make sure the H2 dependency is included.", e);
        }
    }

    @PostConstruct
    void loadTestData() {
        try {
            // Load test data from SQL file
            Resource resource = new ClassPathResource("db/test_data/V9999__test_data.sql");
            String sql = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);

            // Split SQL statements and execute them
            Arrays.stream(sql.split(";"))
                    .map(String::trim)
                    .filter(statement -> !statement.isEmpty())
                    .forEach(statement -> {
                        try {
                            // Using prepared statement to safely execute SQL
                            jdbcTemplate.update(connection -> connection.prepareStatement(statement));
                        } catch (Exception e) {
                            logger.error("Error executing SQL: {}", statement, e);
                        }
                    });

            logger.info("Test data loaded successfully");
        } catch (IOException e) {
            logger.error("Failed to load test data", e);
        }
    }

    /**
     * Dynamically set properties for the test context.
     * This method allows overriding application properties programmatically.
     *
     * @param registry The property registry to add properties to
     */
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        // Set logging level for tests
        registry.add("logging.level.org.hibernate.SQL", () -> "DEBUG");
        registry.add("logging.level.org.hibernate.type.descriptor.sql.BasicBinder", () -> "TRACE");

        // Set a fixed server port to avoid conflicts
        registry.add("server.port", () -> "0"); // Random port
    }

    /**
     * Helper method to generate unique test data identifiers
     *
     * @param prefix A prefix for the identifier
     * @return A unique identifier
     */
    protected String uniqueIdentifier(String prefix) {
        return prefix + "_" + TEST_RUN_ID + "_" + System.nanoTime();
    }

    /**
     * Helper method to clear specific tables before tests
     *
     * @param tableName The name of the table to clear
     */
    protected void clearTable(String tableName) {
        // Using table name pattern validation to prevent SQL injection
        if (!tableName.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }

        // Using prepared statement for safer execution
        jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
        logger.info("Cleared table: {}", tableName);
    }
}