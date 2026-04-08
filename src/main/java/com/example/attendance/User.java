package com.example.attendance;

import java.time.LocalDateTime;

public class User {
    private Integer userId;
    private String username;
    private String password;
    // 对应role字段（角色：admin/teacher/student）
    private String role;
    // 对应name字段（真实姓名）
    private String name;
    // 对应create_time字段（创建时间）
    private LocalDateTime createTime;

    public User() {}

    public User(Integer userId, String username, String password, String role, String name, LocalDateTime createTime) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.createTime = createTime;
    }

    // 所有字段的Getter和Setter方法（必须完整，否则Spring无法读写数据）
    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
