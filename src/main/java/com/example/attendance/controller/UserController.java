package com.example.attendance.controller;

import com.example.attendance.Student;
import com.example.attendance.User;
import com.example.attendance.repository.StudentRepository;
import com.example.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String register(@RequestParam String username,
                           @RequestParam String name,
                           @RequestParam String password,
                           @RequestParam(required = false) String className,
                           @RequestParam(required = false) String phone,
                           RedirectAttributes redirectAttributes) {
        try {
            // 检查用户名是否已存在
            if (userRepository.findByUsername(username) != null) {
                redirectAttributes.addFlashAttribute("errorMsg", "该学号已注册！");
                return "redirect:/register";
            }

            // 创建 User 账号
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole("student");
            user.setName(name);
            userRepository.save(user);

            // 创建 Student 记录（同步姓名、班级、电话）
            Student student = new Student();
            student.setStudentId(username);
            student.setName(name);
            student.setClassName(className != null ? className : "未分班");
            student.setPhone(phone != null ? phone : "");
            student.setAttendanceCount(0);
            studentRepository.save(student);

            redirectAttributes.addFlashAttribute("msg", "注册成功！请登录");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "注册失败：" + e.getMessage());
            return "redirect:/register";
        }
    }
}