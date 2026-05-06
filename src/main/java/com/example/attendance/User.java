package com.example.attendance;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user") // 和你的数据库表名完全一致
public class User {

    // 主键：对应数据库的 user_id 列，且是自增的
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "role", length = 20)
    private String role;

    @Column(name = "name", length = 50)
    private String name;
}