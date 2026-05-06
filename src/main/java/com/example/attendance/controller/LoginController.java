package com.example.attendance.controller;

import com.example.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // 注意：这里用@Controller，不是@RestController，用于返回视图
public class LoginController {

    @Autowired
    private UserService userService;

    // 处理GET请求，返回登录页面
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String error,
            Model model) {
        model.addAttribute("title", "用户登录");
        // 处理登录错误，传递错误信息到页面
        if (error != null) {
            model.addAttribute("errorMsg", "用户名或密码错误");
        }
        return "login"; // 对应templates下的login.html
    }

    // 处理POST请求，处理登录表单提交
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password) {

        // 调用UserService验证用户名密码
        if (userService.authenticate(username, password)) {
            // 登录成功：重定向到系统首页
            return "redirect:/dashboard";
        }
        // 登录失败：重定向到登录页，并带error参数
        return "redirect:/login?error=true";
    }

    // 系统首页控制器
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("title", "系统首页");
        return "index"; // 对应templates下的index.html
    }
}