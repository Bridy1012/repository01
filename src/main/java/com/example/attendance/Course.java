package com.example.attendance;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "course_name", length = 10, nullable = false)
    private String courseName;

    @Column(name = "teacher_id")
    private Integer teacherId;

    @Column(name = "classroom", length = 50)
    private String classroom;

    @Column(name = "seat_layout", columnDefinition = "TEXT")
    private String seatLayout;

    @Column(name = "week_day")
    private Integer weekDay;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;
}