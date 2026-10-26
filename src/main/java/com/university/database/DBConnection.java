package com.university.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mindrot.jbcrypt.BCrypt;

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
    private static final String DRIVER_H2 = "org.h2.Driver";
    private static final String DRIVER_MYSQL = "com.mysql.cj.jdbc.Driver";
    private static final String DEFAULT_MYSQL_URL =
            "jdbc:mysql://localhost:3306/university_db?sslMode=PREFERRED&serverTimezone=UTC";

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

            String url = resolve(props, "db.url", "JDBC_URL", DEFAULT_MYSQL_URL);
                String username = resolve(props, "db.username", "DB_USERNAME", null);
                String password = resolve(props, "db.password", "DB_PASSWORD", null);

                if (isBlank(url) || isBlank(username) || password == null) {
                throw new IllegalStateException("Database settings must be supplied through db.properties " +
                    "or JDBC_URL, DB_USERNAME, and DB_PASSWORD environment variables");
                }

            isH2 = url.contains(":h2:");
                if (!isH2 && isBlank(password)) {
                    throw new IllegalStateException("DB_PASSWORD must be set for MySQL connections");
                }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);

            if (isH2) {
                config.setDriverClassName(DRIVER_H2);
                config.setMaximumPoolSize(5);
                logger.info("Configuring H2 database (demo mode)");
            } else {
                config.setDriverClassName(DRIVER_MYSQL);
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
                initializeH2Credentials();
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
                initializeH2Credentials();
            } else {
                logger.error("schema-h2.sql not found on classpath");
            }
        } catch (SQLException e) {
            logger.error("Failed to initialize H2 schema", e);
        }
    }

    private static void initializeH2Credentials() {
        String adminPassword = System.getenv("DEMO_ADMIN_PASSWORD");
        String staffPassword = System.getenv("DEMO_STAFF_PASSWORD");
        String lecturerPassword = System.getenv("DEMO_LECTURER_PASSWORD");
        String studentPassword = System.getenv("DEMO_STUDENT_PASSWORD");
        if (isBlank(adminPassword) || isBlank(staffPassword)
                || isBlank(lecturerPassword) || isBlank(studentPassword)) {
            logger.warn("Demo credentials are not initialized. Set DEMO_ADMIN_PASSWORD, " +
                    "DEMO_STAFF_PASSWORD, DEMO_LECTURER_PASSWORD, and DEMO_STUDENT_PASSWORD.");
            return;
        }

        String userSql = "UPDATE users SET password = ? WHERE username = ?";
        String peopleSql = "UPDATE %s SET password = ?";
        try (Connection connection = dataSource.getConnection();
             java.sql.PreparedStatement userStatement = connection.prepareStatement(userSql);
             java.sql.PreparedStatement studentStatement = connection.prepareStatement(
                     String.format(peopleSql, "students"));
             java.sql.PreparedStatement lecturerStatement = connection.prepareStatement(
                     String.format(peopleSql, "lecturers"))) {
            setCredential(userStatement, adminPassword, "admin");
            setCredential(userStatement, staffPassword, "staff1");
            setCredential(userStatement, staffPassword, "staff2");
            setCredential(userStatement, lecturerPassword, "lecturer1");
            userStatement.executeBatch();

            studentStatement.setString(1, BCrypt.hashpw(studentPassword, BCrypt.gensalt()));
            studentStatement.executeUpdate();
            lecturerStatement.setString(1, BCrypt.hashpw(lecturerPassword, BCrypt.gensalt()));
            lecturerStatement.executeUpdate();
            logger.info("Demo credentials initialized from environment variables.");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to initialize demo credentials", e);
        }
    }

    private static void setCredential(java.sql.PreparedStatement statement, String password, String username)
            throws SQLException {
        statement.setString(1, BCrypt.hashpw(password, BCrypt.gensalt()));
        statement.setString(2, username);
        statement.addBatch();
    }

    private static String resolve(Properties props, String propKey, String envKey, String defaultValue) {
        String propVal = System.getProperty(propKey);
        if (propVal != null && !propVal.isEmpty()) {
            return propVal;
        }
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.isEmpty()) {
            return envVal;
        }
        return props.getProperty(propKey, defaultValue);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static javax.sql.DataSource getDataSource() {
        return dataSource;
    }

    public static String getDriverClassName() {
        return isH2 ? DRIVER_H2 : DRIVER_MYSQL;
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