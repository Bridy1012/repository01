package com.example.attendance.service.impl;

import com.example.attendance.Attendance;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.service.AttendanceService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    // 原有CRUD实现不变
    @Override
    public Attendance saveAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @Override
    public Optional<Attendance> getAttendanceById(Long id) {
        return attendanceRepository.findById(id);
    }

    @Override
    public List<Attendance> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    @Override
    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }

    // 分页+多条件查询实现
    @Override
    public Page<Attendance> pageAttendances(String studentId, LocalDateTime startDate, LocalDateTime endDate, String status, Pageable pageable) {
        // 构建动态查询条件（Specification）
        Specification<Attendance> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 按学号筛选
            if (studentId != null && !studentId.isBlank()) {
                predicates.add(cb.equal(root.get("student").get("studentId"), studentId));
            }

            // 按考勤日期起始筛选
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("attendanceDate"), startDate));
            }

            // 按考勤日期结束筛选
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("attendanceDate"), endDate));
            }

            // 按考勤状态筛选
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // 组合所有条件（没有条件则返回null，查询全部）
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 带条件+分页+排序查询
        return attendanceRepository.findAll(spec, pageable);
    }
}