package com.example.attendance.service;

import com.example.attendance.User;
import java.util.List;

public interface UserService {
    // 新增用户
    int addUser(User user);
    // 根据ID查询
    User getUserById(Integer userId);
    // 查询所有
    List<User> getAllUsers();
    // 更新用户
    int updateUser(User user);
    // 删除用户
    int deleteUser(Integer userId);
}