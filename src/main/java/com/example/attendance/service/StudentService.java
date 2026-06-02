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

    // 新增：批量删除
    void batchDelete(List<String> studentIds);
    // 新增：排序查询
    List<Student> findAllSorted(Sort sort);
    // 新增：搜索+排序
    List<Student> searchStudentsSorted(String keyword, Sort sort);
}