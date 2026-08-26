package com.university.database;

import com.university.model.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class SessionFactoryManager {

    private static final Logger logger = LoggerFactory.getLogger(SessionFactoryManager.class);
    private static volatile SessionFactory sessionFactory;

    private SessionFactoryManager() {
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            synchronized (SessionFactoryManager.class) {
                if (sessionFactory == null || sessionFactory.isClosed()) {
                    sessionFactory = buildSessionFactory();
                }
            }
        }
        return sessionFactory;
    }

    private static SessionFactory buildSessionFactory() {
        try {
            Properties dbProps = loadDbProperties();

            Configuration configuration = new Configuration();

            configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER,
                    "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
            configuration.setProperty(AvailableSettings.URL, dbProps.getProperty("url"));
            configuration.setProperty(AvailableSettings.USER, dbProps.getProperty("username"));
            configuration.setProperty(AvailableSettings.PASS, dbProps.getProperty("password"));
            configuration.setProperty(AvailableSettings.DRIVER, dbProps.getProperty("driver"));

            boolean isH2 = dbProps.getProperty("url").contains(":h2:");
            configuration.setProperty(AvailableSettings.DIALECT,
                    isH2 ? "org.hibernate.dialect.H2Dialect" : "org.hibernate.dialect.MySQLDialect");
            configuration.setProperty(AvailableSettings.SHOW_SQL, "false");
            configuration.setProperty(AvailableSettings.FORMAT_SQL, "true");
            configuration.setProperty(AvailableSettings.HBM2DDL_AUTO, "none");
            configuration.setProperty(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");

            configuration.setProperty("hibernate.hikari.maximumPoolSize", isH2 ? "5" : "10");
            configuration.setProperty("hibernate.hikari.minimumIdle", isH2 ? "1" : "2");
            configuration.setProperty("hibernate.hikari.connectionTimeout", "5000");
            configuration.setProperty("hibernate.hikari.idleTimeout", "600000");
            configuration.setProperty("hibernate.hikari.maxLifetime", "1800000");

            configuration.addAnnotatedClass(Student.class);
            configuration.addAnnotatedClass(Lecturer.class);
            configuration.addAnnotatedClass(Course.class);
            configuration.addAnnotatedClass(Department.class);
            configuration.addAnnotatedClass(Enrollment.class);
            configuration.addAnnotatedClass(User.class);

            SessionFactory factory = configuration.buildSessionFactory();
            logger.info("Hibernate SessionFactory initialized with HikariCP.");
            return factory;
        } catch (Exception e) {
            logger.error("Failed to initialize Hibernate SessionFactory", e);
            throw new ExceptionInInitializerError("SessionFactory initialization failed: " + e.getMessage());
        }
    }

    private static Properties loadDbProperties() {
        Properties props = new Properties();
        try (InputStream is = SessionFactoryManager.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            logger.warn("Could not load db.properties", e);
        }

        Properties result = new Properties();
        String url = resolveEnv(props, "db.url", "JDBC_URL",
                "jdbc:mysql://localhost:3306/university_db?useSSL=false&serverTimezone=UTC");
        String username = resolveEnv(props, "db.username", "DB_USERNAME", "root");
        String password = resolveEnv(props, "db.password", "DB_PASSWORD", "");

        result.setProperty("url", url);
        result.setProperty("username", username);
        result.setProperty("password", password);
        result.setProperty("driver", url.contains(":h2:") ? "org.h2.Driver" : "com.mysql.cj.jdbc.Driver");
        return result;
    }

    private static String resolveEnv(Properties props, String propKey, String envKey, String defaultValue) {
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.isEmpty()) {
            return envVal;
        }
        return props.getProperty(propKey, defaultValue);
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            logger.info("Hibernate SessionFactory closed.");
        }
    }
}
