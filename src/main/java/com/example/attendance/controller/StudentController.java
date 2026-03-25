package com.example.attendance.controller;

import com.example.attendance.Result;
import com.example.attendance.Student;
import com.example.attendance.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student")
public class StudentController {
    // 注入Service层依赖
    @Autowired
    private StudentService studentService;
    // 新增学生接口（仅负责接收请求和返回结果）
    @PostMapping("/add")
    public Result<String> addStudent(@RequestBody Student student) {
        try {
            studentService.addStudent(student);
            return Result.success("学生新增成功！");
        } catch (IllegalArgumentException e) {
            // 捕获 Service 层的校验异常，返回错误结果
            return Result.error(400, e.getMessage());
        }
    }

    // 学生数据
    private static final Map<String, String> student = new HashMap<>();
    static {
        student.put("studentId", "42411181");
        student.put("name", "李佳玉");
        student.put("className", "24级网络空间安全");
    }

    // 任务一：GET接口 /student/info
    @GetMapping("/student/info")
    public String getStudentInfo() {
        return "姓名：李佳玉，学号：42411181，班级：24级网络空间安全";
    }

    // 任务二：POST接口 /student/attendance
    @PostMapping("/student/attendance")
    public String checkAttendance(@RequestBody String studentId) {
        return "学号为 " + studentId + " 的学生打卡成功！";
    }

    // 任务三：GET接口 /student/courses，返回课程列表
    @GetMapping("/student/courses")
    public List<String> getCourses() {
        return Arrays.asList(
                "JAVE EE开发实践",
                "计算机网络",
                "数据库原理与应用",
                "机器学习与数据挖掘"
        );
    }

    // 任务一：接收 路径参数 /student/info/{studentId}
    @GetMapping("/student/info/{studentId}")
    public Result<Map<String, String>> getStudentInfoById(@PathVariable String studentId) {
        return Result.success(student);
    }

    // 任务二：接收 查询参数 /student/list
    @GetMapping("/student/list")
    public Result<Map<String, String>> getStudentByClass(@RequestParam String className) {
        return Result.success(student);
    }

    // 任务三：接收 JSON参数 /attendance/update
    @PostMapping("/attendance/update")
    public Result<String> updateAttendance(@RequestBody Map<String, String> params) {
        String studentId = params.get("studentId");
        String status = params.get("status");
        String msg = "学号：" + studentId + "，打卡状态：" + status + " → 更新成功！";
        return Result.success(msg);
    }
}