package com.example.attendance.repository;

import com.example.attendance.Student;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {
    Student findByStudentId(String studentId);
    List<Student> findByStudentIdContainingOrNameContaining(String studentId, String name);
    List<Student> findByStudentIdContainingOrNameContaining(String studentId, String name, Sort sort);
}