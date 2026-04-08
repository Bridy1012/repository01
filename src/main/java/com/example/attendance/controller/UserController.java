package com.example.attendance.controller;

import com.example.attendance.Result;
import com.example.attendance.User;
import com.example.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REST接口控制器，根路径 /user
@RestController
@RequestMapping("/user")
public class UserController {

    // 注入UserService业务层
    @Autowired
    private UserService userService;

    // 1. 新增用户（POST请求）
    @PostMapping("/add")
    public Result<Integer> addUser(@RequestBody User user) {
        try {
            int userId = userService.addUser(user);
            return Result.success(userId);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        }
    }

    // 2. 根据ID查询用户（GET请求，路径传参）
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    // 3. 查询所有用户（GET请求）
    @GetMapping("/list")
    public Result<List<User>> getAllUsers() {
        List<User> userList = userService.getAllUsers();
        return Result.success(userList);
    }

    // 4. 更新用户（PUT请求）
    @PutMapping("/update")
    public Result<String> updateUser(@RequestBody User user) {
        try {
            userService.updateUser(user);
            return Result.success("用户更新成功！");
        } catch (Exception e) {
            return Result.error(400, "用户更新失败：" + e.getMessage());
        }
    }

    // 5. 删除用户（DELETE请求，路径传参）
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return Result.success("用户删除成功！");
    }
}