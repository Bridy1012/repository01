package com.example.attendance.service.impl;

import com.example.attendance.User;
import com.example.attendance.dto.RegisterRequest;
import com.example.attendance.repository.UserRepository;
import com.example.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User register(RegisterRequest request) {
        // 你之前的注册逻辑不变
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在，请换一个");
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);
        user.setRole("student");
        user.setName(request.getUsername());
        return userRepository.save(user);
    }

    // 实现登录验证：用BCrypt验证密码
    @Override
    public boolean authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return false; // 用户名不存在
        }
        // 用加密器验证明文密码和数据库密文是否匹配
        return passwordEncoder.matches(password, user.getPassword());
    }
}