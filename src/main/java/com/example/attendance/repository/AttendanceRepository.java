package com.example.attendance.repository;

import com.example.attendance.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

// 关键：同时继承 JpaRepository + JpaSpecificationExecutor
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>, JpaSpecificationExecutor<Attendance> {
    // 无需额外写方法，JPA 会自动提供 findAll(Specification, Pageable)
}