package com.example.attendance.repository;

import com.example.attendance.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // 登录查询用户
    User findByUsername(String username);
    // 学生搜索用
    boolean existsByUsername(String username);
}