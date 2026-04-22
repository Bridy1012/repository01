package com.example.attendance;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id", nullable = false)
    private Long attendanceId;

    // 修复1：将LAZY改为EAGER，避免懒加载序列化异常
    // 修复2：加@JsonManagedReference，解决循环引用
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonManagedReference
    private Student student;

    @Column(name = "attendance_date", nullable = false)
    private LocalDateTime attendanceDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "remark", length = 200)
    private String remark;

    // JPA强制要求：无参构造
    public Attendance() {}

    // 全参构造（可选）
    public Attendance(Student student, LocalDateTime attendanceDate, String status, String remark) {
        this.student = student;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.remark = remark;
    }

    // 完整Getter/Setter
    public Long getAttendanceId() { return attendanceId; }
    public void setAttendanceId(Long attendanceId) { this.attendanceId = attendanceId; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public LocalDateTime getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDateTime attendanceDate) { this.attendanceDate = attendanceDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}