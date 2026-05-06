package com.example.attendance.service;

import com.example.attendance.User;
import com.example.attendance.dto.RegisterRequest;

public interface UserService {
    User register(RegisterRequest request);
    // 新增：登录验证方法
    boolean authenticate(String username, String password);
}