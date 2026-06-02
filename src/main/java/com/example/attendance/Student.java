package com.example.attendance;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "student")
public class Student {

    @Id
    @Column(name = "student_id")
    private String studentId;

    private String name;

    @Column(name = "class_name")
    private String className = "";   // 默认空字符串，避免 null

    @Column(name = "attendance_count", nullable = false)
    private Integer attendanceCount = 0;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    private String gender = "";

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String phone = "";

    // Lombok @Data 已生成无参构造、getter/setter
}