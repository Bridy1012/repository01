package com.example.attendance;

public class AttendanceRecord {
    private String studentId;
    private String date;
    private String status; // 如 "已打卡"

    // getter 和 setter
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}