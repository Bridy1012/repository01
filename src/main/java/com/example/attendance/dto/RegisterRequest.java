package com.example.attendance.dto;

import lombok.Data;

@Data // Lombok自动生成Getter/Setter
public class RegisterRequest {
    private String username;
    private String password;
}