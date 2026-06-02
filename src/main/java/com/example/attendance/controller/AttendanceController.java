package com.example.attendance.controller;

import com.example.attendance.AttendanceRecord;
import com.example.attendance.Course;
import com.example.attendance.Student;
import com.example.attendance.User;
import com.example.attendance.repository.StudentRepository;
import com.example.attendance.repository.UserRepository;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.CourseService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Controller
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseService courseService;

    @GetMapping("/attendance")
    public String toAttendancePage(Model model, Principal principal) {
        String studentId = principal.getName();
        Student student = studentRepository.findByStudentId(studentId);
        if (student == null) {
            student = new Student();
            student.setStudentId(studentId);
            student.setName("学生" + studentId);
            student.setAttendanceCount(0);
            student.setClassName("未分班");   // 设置默认班级
            studentRepository.save(student);
        }
        List<Course> courses = courseService.findAll();
        model.addAttribute("student", student);
        model.addAttribute("courses", courses);
        return "attendance";
    }

    @PostMapping("/attendance/checkin")
    public String checkIn(
            @RequestParam Long courseId,
            @RequestParam String courseName,
            @RequestParam(required = false) String remark,
            Principal principal,
            Model model) {
        try {
            String studentId = principal.getName();
            Student student = studentRepository.findByStudentId(studentId);
            if (student == null) {
                throw new RuntimeException("学生信息不存在，请重新注册");
            }
            AttendanceRecord record = attendanceService.checkIn(
                    studentId,
                    student.getName(),
                    courseId,
                    courseName,
                    remark
            );
            model.addAttribute("msg", "✅ 打卡成功：" + record.getStatus());
            model.addAttribute("type", "success");
        } catch (Exception e) {
            model.addAttribute("msg", "❌ " + e.getMessage());
            model.addAttribute("type", "error");
        }
        model.addAttribute("student", studentRepository.findByStudentId(principal.getName()));
        model.addAttribute("courses", courseService.findAll());
        return "attendance";
    }

    @PostMapping("/attendance/checkout")
    public String checkOut(
            @RequestParam String courseName,
            Principal principal,
            Model model) {
        try {
            String studentId = principal.getName();
            attendanceService.checkOut(studentId, courseName);
            model.addAttribute("msg", "✅ 早退提交成功！");
            model.addAttribute("type", "success");
        } catch (Exception e) {
            model.addAttribute("msg", "❌ " + e.getMessage());
            model.addAttribute("type", "error");
        }
        model.addAttribute("student", studentRepository.findByStudentId(principal.getName()));
        model.addAttribute("courses", courseService.findAll());
        return "attendance";
    }

    @GetMapping("/attendance/list")
    public String attendanceList(
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String timeType,
            @RequestParam(required = false) String status,
            Principal principal,
            Model model) {
        List<AttendanceRecord> list;
        String username = principal.getName();
        User loginUser = userRepository.findByUsername(username);

        if ("teacher".equals(loginUser.getRole())) {
            if (courseName != null && !courseName.isEmpty()) {
                list = attendanceService.findByCourseName(courseName);
            } else {
                list = attendanceService.findAllAttendance();
            }
        } else {
            list = attendanceService.quickFilter(username, timeType, status, courseName);
        }

        model.addAttribute("attendanceList", list);
        model.addAttribute("courses", courseService.findAll());
        return "attendance-list";
    }

    @GetMapping("/attendance/export")
    public void exportAttendance(
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String timeType,
            Principal principal,
            HttpServletResponse response) throws Exception {
        String username = principal.getName();
        User loginUser = userRepository.findByUsername(username);
        List<AttendanceRecord> list;

        if ("teacher".equals(loginUser.getRole())) {
            if (courseName != null && !courseName.isEmpty()) {
                list = attendanceService.findByCourseName(courseName);
            } else {
                list = attendanceService.findAllAttendance();
            }
        } else {
            list = attendanceService.quickFilter(username, timeType, null, courseName);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=attendance.xlsx");
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("考勤记录");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("学号");
        header.createCell(1).setCellValue("姓名");
        header.createCell(2).setCellValue("课程");
        header.createCell(3).setCellValue("打卡时间");
        header.createCell(4).setCellValue("早退时间");
        header.createCell(5).setCellValue("状态");

        int rowNum = 1;
        for (AttendanceRecord r : list) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(r.getStudentId());
            row.createCell(1).setCellValue(r.getStudentName());
            row.createCell(2).setCellValue(r.getCourseName());
            row.createCell(3).setCellValue(r.getCheckInTime().toString());
            row.createCell(4).setCellValue(r.getCheckOutTime() == null ? "无" : r.getCheckOutTime().toString());
            row.createCell(5).setCellValue(r.getStatus());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}