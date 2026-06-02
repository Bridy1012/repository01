package com.example.attendance.controller;

import com.example.attendance.Course;
import com.example.attendance.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("courses", courseService.findAll());
        return "course-list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("course", new Course());
        return "course-add";
    }

    @PostMapping("/add")
    public String addSubmit(Course course, RedirectAttributes ra) {
        try {
            if (courseService.existsByCourseName(course.getCourseName())) {
                ra.addFlashAttribute("msg", "课程名称已存在！");
                return "redirect:/courses/add";
            }
            // 如果 teacherId 为 0，转为 null（避免外键约束冲突）
            if (course.getTeacherId() != null && course.getTeacherId() == 0) {
                course.setTeacherId(null);
            }
            courseService.save(course);
            ra.addFlashAttribute("msg", "新增课程成功！");
            return "redirect:/courses";
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("msg", "新增失败：" + e.getMessage());
            return "redirect:/courses/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Course course = courseService.findById(id).orElse(null);
        model.addAttribute("course", course);
        return "course-edit";
    }

    @PostMapping("/edit")
    public String editSubmit(Course course, RedirectAttributes ra) {
        try {
            courseService.save(course);
            ra.addFlashAttribute("msg", "修改课程成功");
        } catch (Exception e) {
            ra.addFlashAttribute("msg", "修改失败：" + e.getMessage());
        }
        return "redirect:/courses";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            courseService.deleteById(id);
            ra.addFlashAttribute("msg", "删除课程成功");
        } catch (Exception e) {
            ra.addFlashAttribute("msg", "删除失败：" + e.getMessage());
        }
        return "redirect:/courses";
    }
}