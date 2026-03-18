package com.example.attendance;

public class Student {
    private String studentId;
    private String name;
    private String className;
    private int attendanceCount;

    public Student() {}
    public Student(String studentId, String name, String className, int attendanceCount) {
        this.studentId = studentId;
        this.name = name;
        this.className = className;
        this.attendanceCount = attendanceCount;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public int getAttendanceCount() { return attendanceCount; }
    public void setAttendanceCount(int attendanceCount) { this.attendanceCount = attendanceCount; }
}