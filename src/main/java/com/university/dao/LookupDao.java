package com.university.dao;

import com.university.database.SessionFactoryManager;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LookupDao {

    private static final Logger logger = LoggerFactory.getLogger(LookupDao.class);

    public List<String> getOptions(String category) {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            NativeQuery<String> query = session.createNativeQuery(
                    "SELECT lookup_value FROM system_lookup WHERE lookup_category = :cat AND is_active = 1 ORDER BY sort_order",
                    String.class);
            query.setParameter("cat", category);
            return new ArrayList<>(query.getResultList());
        } catch (Exception e) {
            logger.error("Failed to retrieve lookup options for category: {}", category, e);
            return new ArrayList<>();
        }
    }

    public String[] getOptionsArray(String category) {
        List<String> list = getOptions(category);
        return list.toArray(new String[0]);
    }
}
