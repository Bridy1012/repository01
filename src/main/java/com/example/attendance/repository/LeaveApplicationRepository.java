package com.example.attendance.repository;

import com.example.attendance.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByStudentIdOrderByApplyTimeDesc(String studentId);
    List<LeaveApplication> findByStatusOrderByApplyTimeAsc(String status);
    List<LeaveApplication> findByStudentIdInAndStatus(List<String> studentIds, String status);
}