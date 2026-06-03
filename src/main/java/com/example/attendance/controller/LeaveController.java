package com.example.attendance.controller;

import com.example.attendance.LeaveApplication;
import com.example.attendance.Student;
import com.example.attendance.repository.StudentRepository;
import com.example.attendance.service.CourseService;
import com.example.attendance.service.LeaveApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/leave")
public class LeaveController {

    @Autowired
    private LeaveApplicationService leaveService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseService courseService;

    // 学生请假申请页面
    @GetMapping("/apply")
    public String applyForm(Model model, Principal principal) {
        String studentId = principal.getName();
        Student student = studentRepository.findByStudentId(studentId);
        model.addAttribute("student", student);
        model.addAttribute("courses", courseService.findAll());
        model.addAttribute("leave", new LeaveApplication());
        return "leave-apply";
    }

    // 学生提交请假申请
    @PostMapping("/apply")
    public String applySubmit(LeaveApplication leave, Principal principal, RedirectAttributes ra) {
        String studentId = principal.getName();
        Student student = studentRepository.findByStudentId(studentId);
        leave.setStudentId(studentId);
        leave.setStudentName(student.getName());
        leaveService.apply(leave);
        ra.addFlashAttribute("msg", "请假申请已提交，请等待老师审批");
        return "redirect:/leave/my";
    }

    // 学生查看自己的请假记录
    @GetMapping("/my")
    public String myLeaves(Model model, Principal principal) {
        String studentId = principal.getName();
        List<LeaveApplication> leaves = leaveService.getMyLeaves(studentId);
        model.addAttribute("leaves", leaves);
        return "leave-my";
    }

    // 教师查看待审批请假列表
    @GetMapping("/pending")
    public String pendingLeaves(Model model) {
        List<LeaveApplication> leaves = leaveService.getPendingLeaves();
        model.addAttribute("leaves", leaves);
        return "leave-pending";
    }

    // 教师审批通过
    @PostMapping("/approve")
    public String approve(@RequestParam Long id, @RequestParam(required = false) String comment, RedirectAttributes ra) {
        leaveService.approve(id, comment);
        ra.addFlashAttribute("msg", "已批准请假");
        return "redirect:/leave/pending";
    }

    // 教师审批拒绝
    @PostMapping("/reject")
    public String reject(@RequestParam Long id, @RequestParam(required = false) String comment, RedirectAttributes ra) {
        leaveService.reject(id, comment);
        ra.addFlashAttribute("msg", "已拒绝请假");
        return "redirect:/leave/pending";
    }
}