package com.example.attendance.service.impl;

import com.example.attendance.Student;
import com.example.attendance.repository.StudentRepository;
import com.example.attendance.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    public void save(Student student) {
        studentRepository.save(student);
    }

    @Override
    public Student findById(String studentId) {
        return studentRepository.findById(studentId).orElse(null);
    }

    @Override
    public void deleteById(String studentId) {
        studentRepository.deleteById(studentId);
    }

    @Override
    public List<Student> searchStudents(String keyword) {
        return studentRepository.findByStudentIdContainingOrNameContaining(keyword, keyword);
    }

    @Override
    public boolean existsByStudentId(String studentId) {
        return studentRepository.existsById(studentId);
    }

    // 批量删除实现
    @Override
    public void batchDelete(List<String> studentIds) {
        studentRepository.deleteAllById(studentIds);
    }

    // 排序查询实现
    @Override
    public List<Student> findAllSorted(Sort sort) {
        return studentRepository.findAll(sort);
    }

    // 搜索+排序实现
    @Override
    public List<Student> searchStudentsSorted(String keyword, Sort sort) {
        return studentRepository.findByStudentIdContainingOrNameContaining(keyword, keyword, sort);
    }
}