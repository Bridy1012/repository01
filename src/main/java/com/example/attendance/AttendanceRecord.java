package com.example.attendance;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "attendance_record")
public class AttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentId;
    private String studentName;
    private String courseName;
    private Long courseId;
    private LocalDateTime checkInTime;
    private LocalDate checkInDate;
    private String status;
    private String remark;
    private LocalDateTime checkOutTime;
}