package com.example.attendance.service;

import com.example.attendance.AttendanceRecord;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    // 打卡：增加 courseId 参数
    AttendanceRecord checkIn(String studentId, String studentName, Long courseId, String courseName, String remark);

    List<AttendanceRecord> findByStudentId(String studentId);

    List<AttendanceRecord> filterAttendance(LocalDate startDate, LocalDate endDate, String status, String courseName);

    AttendanceRecord checkOut(String studentId, String courseName);

    List<AttendanceRecord> quickFilter(String studentId, String type, String status, String courseName);

    List<AttendanceRecord> findAllAttendance();

    List<AttendanceRecord> findByCourseName(String courseName);
}