package com.example.attendance.service;

import com.example.attendance.Student;
import java.util.List;
import java.util.Optional;

public interface StudentService {
    // 新增/修改学生
    Student saveStudent(Student student);
    // 根据学号查询
    Optional<Student> getStudentById(String studentId);
    // 查询所有学生
    List<Student> getAllStudents();
    // 删除学生
    void deleteStudent(String studentId);
}