package com.example.attendance.service;

import com.example.attendance.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceService {
    // 原有CRUD方法不变
    Attendance saveAttendance(Attendance attendance);
    Optional<Attendance> getAttendanceById(Long id);
    List<Attendance> getAllAttendances();
    void deleteAttendance(Long id);

    // 新增：分页+排序+多条件查询
    Page<Attendance> pageAttendances(
            String studentId,       // 学号筛选（可选）
            LocalDateTime startDate,// 考勤日期起始（可选）
            LocalDateTime endDate,  // 考勤日期结束（可选）
            String status,          // 考勤状态（可选）
            Pageable pageable       // 分页+排序参数
    );
}