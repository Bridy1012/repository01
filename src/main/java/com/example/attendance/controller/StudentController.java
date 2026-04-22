package com.example.attendance.controller;

import com.example.attendance.Result;
import com.example.attendance.Student;
import com.example.attendance.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // 1. 新增/修改学生
    @PostMapping("/save")
    public Result<Student> saveStudent(@RequestBody Student student) {
        Student save = studentService.saveStudent(student);
        return Result.success(save);
    }

    // 2. 根据学号查询学生
    @GetMapping("/{id}")
    public Result<Student> getStudentById(@PathVariable String id) {
        Optional<Student> student = studentService.getStudentById(id);
        return student.map(Result::success).orElseGet(() -> Result.error(404, "学生不存在"));
    }

    // 3. 查询所有学生
    @GetMapping("/list")
    public Result<List<Student>> getAllStudents() {
        List<Student> list = studentService.getAllStudents();
        return Result.success(list);
    }

    // 4. 删除学生
    @DeleteMapping("/{id}")
    public Result<String> deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return Result.success("删除成功");
    }
}