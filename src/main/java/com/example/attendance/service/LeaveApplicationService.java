package com.example.attendance.service;

import com.example.attendance.LeaveApplication;
import java.util.List;

public interface LeaveApplicationService {
    void apply(LeaveApplication leave);
    List<LeaveApplication> getMyLeaves(String studentId);
    List<LeaveApplication> getPendingLeaves();
    void approve(Long id, String teacherComment);
    void reject(Long id, String teacherComment);
}