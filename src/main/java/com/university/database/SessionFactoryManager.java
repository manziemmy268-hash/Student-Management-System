package com.university.database;

import com.university.model.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            Configuration configuration = new Configuration();

            configuration.getProperties().put(AvailableSettings.DATASOURCE, DBConnection.getDataSource());

            boolean isH2 = DBConnection.isH2();
            configuration.setProperty(AvailableSettings.DIALECT,
                    isH2 ? "org.hibernate.dialect.H2Dialect" : "org.hibernate.dialect.MySQLDialect");
            configuration.setProperty(AvailableSettings.SHOW_SQL, "false");
            configuration.setProperty(AvailableSettings.FORMAT_SQL, "true");
            configuration.setProperty(AvailableSettings.HBM2DDL_AUTO, "none");
            configuration.setProperty(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");

            configuration.addAnnotatedClass(Student.class);
            configuration.addAnnotatedClass(Lecturer.class);
            configuration.addAnnotatedClass(Course.class);
            configuration.addAnnotatedClass(Department.class);
            configuration.addAnnotatedClass(Enrollment.class);
            configuration.addAnnotatedClass(User.class);

            SessionFactory factory = configuration.buildSessionFactory();
            logger.info("Hibernate SessionFactory initialized using shared HikariCP pool.");
            return factory;
        } catch (Exception e) {
            logger.error("Failed to initialize Hibernate SessionFactory", e);
            throw new ExceptionInInitializerError("SessionFactory initialization failed: " + e.getMessage());
        }
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            logger.info("Hibernate SessionFactory closed.");
        }
    }
}