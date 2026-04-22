package com.example.attendance.repository;

import com.example.attendance.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository 注解：标记这是数据访问层
@Repository
// 继承 JpaRepository<实体类, 主键类型>
// Student：实体类；String：主键 studentId 的类型
public interface StudentRepository extends JpaRepository<Student, String> {

    // 无需写任何代码！JPA 自动提供增删改查所有方法
}