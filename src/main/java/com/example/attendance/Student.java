package com.example.attendance;

import jakarta.persistence.*;

// 1. @Entity：标记这是一个JPA实体类，对应数据库表
@Entity
// 2. @Table：显式指定映射的数据库表名（和你数据库的student表完全对应）
@Table(name = "student")
public class Student {

    // 3. @Id：标记主键字段（学号，手动赋值，无需自增）
    @Id
    // 4. @Column：指定映射的数据库列名，nullable=false表示非空
    @Column(name = "student_id", nullable = false, length = 50)
    private String studentId; // 学号（主键）

    @Column(name = "name", nullable = false, length = 50)
    private String name; // 姓名

    @Column(name = "class_name", nullable = false, length = 100)
    private String className; // 班级

    @Column(name = "attendance_count", nullable = false)
    private int attendanceCount; // 考勤次数

    // 无参构造方法（JPA强制要求，否则无法实例化对象）
    public Student() {}

    // 全参构造方法（方便手动创建对象）
    public Student(String studentId, String name, String className, int attendanceCount) {
        this.studentId = studentId;
        this.name = name;
        this.className = className;
        this.attendanceCount = attendanceCount;
    }

    // 完整Getter/Setter方法（JPA必须，用于读写实体字段）
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getAttendanceCount() {
        return attendanceCount;
    }

    public void setAttendanceCount(int attendanceCount) {
        this.attendanceCount = attendanceCount;
    }
}