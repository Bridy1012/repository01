package com.example.attendance.service;

import com.example.attendance.Course;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    List<Course> findAll();
    Optional<Course> findById(Long id);
    Optional<Course> findByCourseName(String courseName);
    void save(Course course);
    void deleteById(Long id);
    boolean existsByCourseName(String courseName);
}