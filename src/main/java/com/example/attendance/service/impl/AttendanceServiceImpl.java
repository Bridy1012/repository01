package com.example.attendance.service.impl;

import com.example.attendance.AttendanceRecord;
import com.example.attendance.Course;
import com.example.attendance.Student;
import com.example.attendance.repository.AttendanceRecordRepository;
import com.example.attendance.repository.StudentRepository;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;
    @Autowired
    private CourseService courseService;
    @Autowired
    private StudentRepository studentRepository;

    @Override
    @Transactional
    public AttendanceRecord checkIn(String studentId, String studentName, Long courseId, String courseName, String remark) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime nowTime = now.toLocalTime();

        boolean hasCheckIn = attendanceRecordRepository.existsByStudentIdAndCheckInDateAndCourseName(studentId, today, courseName);
        if (hasCheckIn) {
            throw new RuntimeException("❌ 今日该课程已打卡！");
        }

        Course course = courseService.findById(courseId)
                .orElseThrow(() -> new RuntimeException("❌ 课程不存在，请联系老师设置上课时间！"));
        LocalTime startTime = course.getStartTime();
        LocalTime endTime = course.getEndTime();
        if (endTime == null) {
            endTime = startTime.plusHours(2);
        }

        LocalTime allowStart = startTime.minusMinutes(15);
        LocalTime allowEnd = startTime.plusMinutes(30);

        if (nowTime.isBefore(allowStart)) {
            throw new RuntimeException("❌ 打卡未开始，允许打卡时间：" + allowStart + " 之后");
        }
        if (nowTime.isAfter(endTime)) {
            throw new RuntimeException("❌ 课程已结束，无法打卡");
        }

        String status = nowTime.isAfter(allowEnd) ? "迟到" : "正常";

        AttendanceRecord record = new AttendanceRecord();
        record.setStudentId(studentId);
        record.setStudentName(studentName);
        record.setCourseId(courseId);
        record.setCourseName(courseName);
        record.setCheckInTime(now);
        record.setCheckInDate(today);
        record.setStatus(status);
        record.setRemark(remark);

        AttendanceRecord savedRecord = attendanceRecordRepository.save(record);

        Student student = studentRepository.findByStudentId(studentId);
        if (student != null) {
            Integer currentCount = student.getAttendanceCount();
            if (currentCount == null) currentCount = 0;
            student.setAttendanceCount(currentCount + 1);
            studentRepository.save(student);
        }

        return savedRecord;
    }

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
    @Transactional
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
                case "today": start = LocalDate.now(); break;
                case "week": start = LocalDate.now().with(DayOfWeek.MONDAY); break;
                case "month": start = LocalDate.now().withDayOfMonth(1); break;
                default: return filterAttendance(null, null, status, courseName);
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

    @Override
    @Transactional
    public void deleteAttendanceRecord(Long recordId) {
        AttendanceRecord record = attendanceRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("考勤记录不存在"));
        attendanceRecordRepository.delete(record);
    }

    @Override
    @Transactional
    public void batchDeleteAttendanceRecords(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        attendanceRecordRepository.deleteAllById(ids);
    }
}