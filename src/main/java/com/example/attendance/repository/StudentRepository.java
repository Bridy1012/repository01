package com.example.attendance.repository;

import com.example.attendance.Student;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {

    // 1. 考勤功能：根据学号精确查询
    Student findByStudentId(String studentId);

    // 2. 重载方法1：无排序（适配传2个参数的调用）
    List<Student> findByStudentIdContainingOrNameContaining(String studentId, String name);

    // 3. 重载方法2：带排序（适配传3个参数的调用）
    List<Student> findByStudentIdContainingOrNameContaining(String studentId, String name, Sort sort);

}