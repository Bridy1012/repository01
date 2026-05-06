package com.example.attendance.controller;

import com.example.attendance.dto.RegisterRequest;
import com.example.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    // 处理GET请求，返回注册页面
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("title", "用户注册");
        return "register"; // 对应templates下的register.html
    }

    // 处理POST请求，处理注册表单提交
    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        // 1. 验证两次密码是否一致（表单验证）
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMsg", "两次密码输入不一致");
            model.addAttribute("username", username); // 回显用户名
            return "register"; // 返回注册页，显示错误信息
        }

        try {
            // 2. 调用UserService完成注册
            RegisterRequest request = new RegisterRequest();
            request.setUsername(username);
            request.setPassword(password);
            userService.register(request);
            // 注册成功：重定向到登录页
            return "redirect:/login";
        } catch (RuntimeException e) {
            // 注册失败（如用户名已存在）：返回注册页并显示错误
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("username", username);
            return "register";
        }
    }
}