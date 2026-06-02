package com.example.attendance.repository;

import com.example.attendance.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseName(String courseName);
    boolean existsByCourseName(String courseName);
}