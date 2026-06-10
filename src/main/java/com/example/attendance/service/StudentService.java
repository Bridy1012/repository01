package com.example.attendance.service;

import com.example.attendance.Student;
import org.springframework.data.domain.Sort;
import java.util.List;

public interface StudentService {
    List<Student> findAll();
    void save(Student student);
    Student findById(String studentId);
    void deleteById(String studentId);
    List<Student> searchStudents(String keyword);
    boolean existsByStudentId(String studentId);
    void batchDelete(List<String> studentIds);
    List<Student> findAllSorted(Sort sort);
    List<Student> searchStudentsSorted(String keyword, Sort sort);
    void batchSave(List<Student> students);
}