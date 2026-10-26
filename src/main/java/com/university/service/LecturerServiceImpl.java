package com.university.service;

import com.university.dao.LecturerDao;
import com.university.exception.DuplicateEntityException;
import com.university.exception.ValidationException;
import com.university.model.Lecturer;
import com.university.utility.ValidationUtil;
import com.university.utility.InitialPasswordProvider;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LecturerServiceImpl implements LecturerService {

    private static final Logger logger = LoggerFactory.getLogger(LecturerServiceImpl.class);
    private final LecturerDao lecturerDao;

    public LecturerServiceImpl() {
        this.lecturerDao = new LecturerDao();
    }

    public LecturerServiceImpl(LecturerDao lecturerDao) {
        this.lecturerDao = lecturerDao;
    }

    @Override
    public boolean addLecturer(Lecturer lecturer) {
        logger.info("Adding lecturer: {} {}", lecturer.getFirstName(), lecturer.getLastName());

        if (!ValidationUtil.isValidName(lecturer.getFirstName())) {
            throw new ValidationException("firstName", "First name must be at least 2 characters");
        }
        if (!ValidationUtil.isValidName(lecturer.getLastName())) {
            throw new ValidationException("lastName", "Last name must be at least 2 characters");
        }
        if (!ValidationUtil.isValidEmail(lecturer.getEmail())) {
            throw new ValidationException("email", "Invalid email format");
        }
        if (lecturer.getPhone() != null && !lecturer.getPhone().isEmpty()
                && !ValidationUtil.isValidPhone(lecturer.getPhone())) {
            throw new ValidationException("phone", "Invalid phone number format");
        }
        if (isEmailDuplicate(lecturer.getEmail(), 0)) {
            throw new DuplicateEntityException("Lecturer", "email", lecturer.getEmail());
        }

        lecturer.setFirstName(ValidationUtil.sanitizeInput(lecturer.getFirstName()));
        lecturer.setLastName(ValidationUtil.sanitizeInput(lecturer.getLastName()));
        lecturer.setEmail(ValidationUtil.sanitizeInput(lecturer.getEmail()));
        lecturer.setPhone(ValidationUtil.sanitizeInput(lecturer.getPhone()));

        if (lecturer.getPassword() == null || lecturer.getPassword().isEmpty()) {
            lecturer.setPassword(BCrypt.hashpw(InitialPasswordProvider.getRequired(), BCrypt.gensalt()));
        }

        boolean result = lecturerDao.addLecturer(lecturer);
        if (result) {
            logger.info("Lecturer added successfully: {} {}", lecturer.getFirstName(), lecturer.getLastName());
        } else {
            logger.error("Failed to add lecturer: {} {}", lecturer.getFirstName(), lecturer.getLastName());
        }
        return result;
    }

    @Override
    public boolean updateLecturer(Lecturer lecturer) {
        logger.info("Updating lecturer id: {}", lecturer.getLecturerId());

        if (!ValidationUtil.isValidName(lecturer.getFirstName())) {
            throw new ValidationException("firstName", "First name must be at least 2 characters");
        }
        if (!ValidationUtil.isValidName(lecturer.getLastName())) {
            throw new ValidationException("lastName", "Last name must be at least 2 characters");
        }
        if (!ValidationUtil.isValidEmail(lecturer.getEmail())) {
            throw new ValidationException("email", "Invalid email format");
        }
        if (lecturer.getPhone() != null && !lecturer.getPhone().isEmpty()
                && !ValidationUtil.isValidPhone(lecturer.getPhone())) {
            throw new ValidationException("phone", "Invalid phone number format");
        }
        if (isEmailDuplicate(lecturer.getEmail(), lecturer.getLecturerId())) {
            throw new DuplicateEntityException("Lecturer", "email", lecturer.getEmail());
        }

        lecturer.setFirstName(ValidationUtil.sanitizeInput(lecturer.getFirstName()));
        lecturer.setLastName(ValidationUtil.sanitizeInput(lecturer.getLastName()));
        lecturer.setEmail(ValidationUtil.sanitizeInput(lecturer.getEmail()));
        lecturer.setPhone(ValidationUtil.sanitizeInput(lecturer.getPhone()));

        boolean result = lecturerDao.updateLecturer(lecturer);
        if (result) {
            logger.info("Lecturer updated successfully id: {}", lecturer.getLecturerId());
        } else {
            logger.error("Failed to update lecturer id: {}", lecturer.getLecturerId());
        }
        return result;
    }

    @Override
    public boolean deleteLecturer(int lecturerId) {
        logger.info("Deleting lecturer id: {}", lecturerId);
        boolean result = lecturerDao.deleteLecturer(lecturerId);
        if (result) {
            logger.info("Lecturer deleted successfully id: {}", lecturerId);
        } else {
            logger.error("Failed to delete lecturer id: {}", lecturerId);
        }
        return result;
    }

    @Override
    public List<Lecturer> getAllLecturers() {
        return lecturerDao.getAllLecturers();
    }

    @Override
    public Lecturer getLecturerById(int id) {
        return lecturerDao.getLecturerById(id);
    }

    @Override
    public List<Lecturer> searchLecturers(String keyword) {
        return lecturerDao.searchLecturers(keyword);
    }

    @Override
    public int getLecturerCount() {
        return lecturerDao.getLecturerCount();
    }

    @Override
    public boolean isEmailDuplicate(String email, int excludeId) {
        return lecturerDao.isEmailDuplicate(email, excludeId);
    }
}
