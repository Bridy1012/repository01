package com.example.attendance.service.impl;

import com.example.attendance.AttendanceRecord;
import com.example.attendance.Course;
import com.example.attendance.repository.AttendanceRecordRepository;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;

    @Autowired
    private CourseService courseService;

    @Override
    public AttendanceRecord checkIn(String studentId, String studentName, Long courseId, String courseName, String remark) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime nowTime = now.toLocalTime();

        // 重复打卡判断
        boolean hasCheckIn = attendanceRecordRepository.existsByStudentIdAndCheckInDateAndCourseName(studentId, today, courseName);
        if (hasCheckIn) {
            throw new RuntimeException("❌ 今日该课程已打卡！");
        }

        // 获取课程信息（根据课程名，或根据 courseId）
        Course course = courseService.findById(courseId)
                .orElseThrow(() -> new RuntimeException("❌ 课程不存在，请联系老师！"));
        // 验证课程名是否匹配（可选）
        if (!course.getCourseName().equals(courseName)) {
            throw new RuntimeException("❌ 课程名称不匹配！");
        }

        LocalTime startTime = course.getStartTime();
        LocalTime allowStart = startTime.minusMinutes(15);
        LocalTime allowEnd = startTime.plusMinutes(30);

        if (nowTime.isBefore(allowStart) || nowTime.isAfter(allowEnd)) {
            throw new RuntimeException("❌ 不在打卡时间内（" + allowStart + " - " + allowEnd + "）！");
        }

        // 迟到判断：超过上课时间即为迟到
        String status = nowTime.isAfter(startTime) ? "迟到" : "正常";

        AttendanceRecord record = new AttendanceRecord();
        record.setStudentId(studentId);
        record.setStudentName(studentName);
        record.setCourseId(courseId);
        record.setCourseName(courseName);
        record.setCheckInTime(now);
        record.setCheckInDate(today);
        record.setStatus(status);
        record.setRemark(remark);

        return attendanceRecordRepository.save(record);
    }

    // 以下方法保持不变，但需要确保 findByCourseName 已在 Repository 中定义
    @Override
    public List<AttendanceRecord> findByStudentId(String studentId) {
        return attendanceRecordRepository.findByStudentId(studentId);
    }

    @Override
    public List<AttendanceRecord> filterAttendance(LocalDate startDate, LocalDate endDate, String status, String courseName) {
        if (startDate == null) startDate = LocalDate.of(2000, 1, 1);
        if (endDate == null) endDate = LocalDate.now();
        if (courseName == null) courseName = "";

        if (status == null || status.trim().isEmpty()) {
            return attendanceRecordRepository.findByCheckInDateBetweenAndCourseNameContaining(startDate, endDate, courseName);
        } else {
            return attendanceRecordRepository.findByCheckInDateBetweenAndStatusAndCourseNameContaining(startDate, endDate, status, courseName);
        }
    }

    @Override
    public AttendanceRecord checkOut(String studentId, String courseName) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        AttendanceRecord record = attendanceRecordRepository
                .findByStudentIdAndCheckInDateAndCourseName(studentId, today, courseName)
                .orElseThrow(() -> new RuntimeException("❌ 未打卡，无法早退！"));

        if (record.getCheckOutTime() != null) {
            throw new RuntimeException("❌ 已提交早退！");
        }

        record.setCheckOutTime(now);
        record.setStatus("早退");
        return attendanceRecordRepository.save(record);
    }

    @Override
    public List<AttendanceRecord> quickFilter(String studentId, String type, String status, String courseName) {
        LocalDate start = null;
        LocalDate end = LocalDate.now();

        if (type != null) {
            switch (type) {
                case "today":
                    start = LocalDate.now();
                    break;
                case "week":
                    start = LocalDate.now().with(DayOfWeek.MONDAY);
                    break;
                case "month":
                    start = LocalDate.now().withDayOfMonth(1);
                    break;
                default:
                    return filterAttendance(null, null, status, courseName);
            }
            return filterAttendance(start, end, status, courseName);
        } else {
            return filterAttendance(null, null, status, courseName);
        }
    }

    @Override
    public List<AttendanceRecord> findAllAttendance() {
        return attendanceRecordRepository.findAll();
    }

    @Override
    public List<AttendanceRecord> findByCourseName(String courseName) {
        if (courseName == null || courseName.isEmpty()) {
            return attendanceRecordRepository.findAll();
        }
        return attendanceRecordRepository.findByCourseName(courseName);
    }
}