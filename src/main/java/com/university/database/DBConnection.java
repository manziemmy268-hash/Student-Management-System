package com.university.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.stream.Collectors;

public class DBConnection {

    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);
    private static HikariDataSource dataSource;
    private static boolean isH2;

    static {
        try {
            Properties props = new Properties();
            InputStream is = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
            if (is != null) {
                props.load(is);
                is.close();
                logger.info("Loaded database properties from db.properties");
            } else {
                logger.info("db.properties not found, using environment variables with defaults");
            }

            String url = resolve(props, "db.url", "JDBC_URL",
                    "jdbc:mysql://localhost:3306/university_db?useSSL=false&serverTimezone=UTC");
            String username = resolve(props, "db.username", "DB_USERNAME", "root");
            String password = resolve(props, "db.password", "DB_PASSWORD", "");

            isH2 = url.contains(":h2:");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);

            if (isH2) {
                config.setDriverClassName("org.h2.Driver");
                config.setMaximumPoolSize(5);
                logger.info("Configuring H2 database (demo mode)");
            } else {
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                config.setMaximumPoolSize(10);
                config.setMinimumIdle(2);
                config.setConnectionTimeout(5000);
                config.setIdleTimeout(600000);
                config.setMaxLifetime(1800000);
                config.setLeakDetectionThreshold(30000);
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                logger.info("Configuring MySQL database");
            }

            dataSource = new HikariDataSource(config);

            if (isH2) {
                initializeH2Schema();
            }

            logger.info("HikariCP connection pool initialized successfully.");
        } catch (Exception e) {
            logger.error("Failed to initialize connection pool", e);
            throw new ExceptionInInitializerError("Connection pool initialization failed: " + e.getMessage());
        }
    }

    private DBConnection() {
    }

    private static void initializeH2Schema() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            boolean tablesExist = stmt.executeQuery(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'DEPARTMENTS'"
            ).next() && stmt.getResultSet().getInt(1) > 0;

            if (tablesExist) {
                logger.info("H2 schema already initialized.");
                return;
            }

            InputStream schemaStream = DBConnection.class.getClassLoader()
                    .getResourceAsStream("schema-h2.sql");
            if (schemaStream != null) {
                String schema;
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(schemaStream, StandardCharsets.UTF_8))) {
                    schema = reader.lines().collect(Collectors.joining("\n"));
                } catch (java.io.IOException e) {
                    logger.error("Failed to read schema-h2.sql", e);
                    return;
                }

                for (String raw : schema.split(";")) {
                    String cleaned = raw.lines()
                            .filter(line -> !line.trim().startsWith("--"))
                            .collect(Collectors.joining("\n"))
                            .trim();
                    if (!cleaned.isEmpty()) {
                        stmt.execute(cleaned);
                    }
                }
                logger.info("H2 schema initialized with sample data.");
            } else {
                logger.error("schema-h2.sql not found on classpath");
            }
        } catch (SQLException e) {
            logger.error("Failed to initialize H2 schema", e);
        }
    }

    private static String resolve(Properties props, String propKey, String envKey, String defaultValue) {
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.isEmpty()) {
            return envVal;
        }
        return props.getProperty(propKey, defaultValue);
    }

    public static javax.sql.DataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static boolean testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(3);
        } catch (SQLException e) {
            logger.error("Database connection test failed", e);
            return false;
        }
    }

    public static boolean isH2() {
        return isH2;
    }

    public static void shutdown() {
        SessionFactoryManager.shutdown();
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("HikariCP connection pool closed.");
        }
    }
}
