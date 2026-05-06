package com.example.attendance.controller;

import com.example.attendance.Result;
import com.example.attendance.User;
import com.example.attendance.dto.LoginRequest;
import com.example.attendance.dto.RegisterRequest;
import com.example.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    // 注册接口
    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            // 不返回密码给前端
            user.setPassword(null);
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    // 登录接口
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginRequest request) {
        try {
            Authentication authRequest = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            Authentication authResult = authenticationManager.authenticate(authRequest);
            return Result.success("登录成功，用户名：" + authResult.getName());
        } catch (AuthenticationException e) {
            return Result.error(401, "用户名或密码错误");
        }
    }
}