package com.university.service;

import com.university.model.Lecturer;
import java.util.List;

public interface LecturerService {

    List<Lecturer> getAllLecturers();

    Lecturer getLecturerById(int id);

    List<Lecturer> searchLecturers(String keyword);

    boolean addLecturer(Lecturer lecturer);

    boolean updateLecturer(Lecturer lecturer);

    boolean deleteLecturer(int lecturerId);

    int getLecturerCount();

    boolean isEmailDuplicate(String email, int excludeId);
}
