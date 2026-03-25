package com.example.attendance.service.impl;

import com.example.attendance.Student;
import com.example.attendance.dao.StudentDao;
import com.example.attendance.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {

    // 注入 Dao 层依赖
    @Autowired
    private StudentDao studentDao;

    @Override
    public void addStudent(Student student) {
        // 业务校验：学号不能为空
        if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            throw new IllegalArgumentException("学号不能为空！");
        }
        // 调用Dao层执行数据库插入操作
        studentDao.insertStudent(student);
    }
}