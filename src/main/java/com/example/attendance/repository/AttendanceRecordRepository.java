package com.example.attendance.repository;

import com.example.attendance.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findByStudentId(String studentId);
    List<AttendanceRecord> findByCheckInDateBetweenAndCourseNameContaining(LocalDate startDate, LocalDate endDate, String courseName);
    List<AttendanceRecord> findByCheckInDateBetweenAndStatusAndCourseNameContaining(LocalDate startDate, LocalDate endDate, String status, String courseName);
    boolean existsByStudentIdAndCheckInDateAndCourseName(String studentId, LocalDate checkInDate, String courseName);
    Optional<AttendanceRecord> findByStudentIdAndCheckInDateAndCourseName(String studentId, LocalDate checkInDate, String courseName);
    List<AttendanceRecord> findByCourseName(String courseName);
    List<AttendanceRecord> findByStudentIdIn(List<String> studentIds);
    List<AttendanceRecord> findByStudentIdInAndStatus(List<String> studentIds, String status);
}