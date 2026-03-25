package com.example.attendance.dao;

import com.example.attendance.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StudentDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 插入学生数据
    public void insertStudent(Student student) {
        String sql = "INSERT INTO student(student_id, name, class_name) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql,
                student.getStudentId(),
                student.getName(),
                student.getClassName());
    }
}