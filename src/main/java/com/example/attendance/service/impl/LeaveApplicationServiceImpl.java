package com.example.attendance.service.impl;

import com.example.attendance.LeaveApplication;
import com.example.attendance.repository.LeaveApplicationRepository;
import com.example.attendance.service.LeaveApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeaveApplicationServiceImpl implements LeaveApplicationService {

    @Autowired
    private LeaveApplicationRepository leaveRepository;

    @Override
    public void apply(LeaveApplication leave) {
        leave.setApplyTime(LocalDateTime.now());
        leave.setStatus("pending");
        leaveRepository.save(leave);
    }

    @Override
    public List<LeaveApplication> getMyLeaves(String studentId) {
        return leaveRepository.findByStudentIdOrderByApplyTimeDesc(studentId);
    }

    @Override
    public List<LeaveApplication> getPendingLeaves() {
        return leaveRepository.findByStatusOrderByApplyTimeAsc("pending");
    }

    @Override
    @Transactional
    public void approve(Long id, String teacherComment) {
        LeaveApplication leave = leaveRepository.findById(id).orElseThrow(() -> new RuntimeException("请假记录不存在"));
        leave.setStatus("approved");
        leave.setTeacherComment(teacherComment);
        leave.setApproveTime(LocalDateTime.now());
        leaveRepository.save(leave);
    }

    @Override
    @Transactional
    public void reject(Long id, String teacherComment) {
        LeaveApplication leave = leaveRepository.findById(id).orElseThrow(() -> new RuntimeException("请假记录不存在"));
        leave.setStatus("rejected");
        leave.setTeacherComment(teacherComment);
        leave.setApproveTime(LocalDateTime.now());
        leaveRepository.save(leave);
    }
}