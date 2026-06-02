package com.example.attendance.controller;

import com.example.attendance.Student;
import com.example.attendance.User;
import com.example.attendance.repository.StudentRepository;
import com.example.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(User user, Model model) {
        try {
            if (userRepository.findByUsername(user.getUsername()) != null) {
                model.addAttribute("msg", "该账号已注册！");
                return "register";
            }
            user.setRole("student");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setName(user.getUsername());
            userRepository.save(user);

            Student student = new Student();
            student.setStudentId(user.getUsername());
            student.setName("学生" + user.getUsername());
            student.setAttendanceCount(0);
            student.setClassName("未分班");   // 设置默认班级
            studentRepository.save(student);

            model.addAttribute("msg", "注册成功！请登录");
            return "login";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", "注册失败：" + e.getMessage());
            return "register";
        }
    }
}