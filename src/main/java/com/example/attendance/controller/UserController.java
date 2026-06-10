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
                           @RequestParam String password,
                           @RequestParam String name,
                           RedirectAttributes redirectAttributes) {
        try {
            if (userRepository.findByUsername(username) != null) {
                redirectAttributes.addFlashAttribute("errorMsg", "该账号已注册！");
                return "redirect:/register";
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole("student");
            user.setName(name);
            userRepository.save(user);

            Student student = new Student();
            student.setStudentId(username);
            student.setName(name);
            student.setAttendanceCount(0);
            student.setClassName("未分班");
            studentRepository.save(student);

            redirectAttributes.addFlashAttribute("msg", "注册成功！请登录");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "注册失败：" + e.getMessage());
            return "redirect:/register";
        }
    }
}