package com.example.attendance.controller;

import com.example.attendance.Student;
import com.example.attendance.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // 列表 + 搜索 + 排序
    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "studentId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortField);
        List<Student> students;

        if (keyword == null || keyword.trim().isEmpty()) {
            students = studentService.findAllSorted(sort);
        } else {
            students = studentService.searchStudentsSorted(keyword.trim(), sort);
            model.addAttribute("keyword", keyword);
        }

        model.addAttribute("students", students);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "list";
    }

    // 新增页面
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("student", new Student());
        return "add";
    }

    // 新增提交
    @PostMapping("/add")
    public String addSubmit(Student student, RedirectAttributes attributes) {
        if (studentService.existsByStudentId(student.getStudentId())) {
            attributes.addFlashAttribute("msg", "新增失败：学号已存在！");
            attributes.addFlashAttribute("type", "error");
            return "redirect:/students/add";
        }
        studentService.save(student);
        attributes.addFlashAttribute("msg", "新增学生成功！");
        attributes.addFlashAttribute("type", "success");
        return "redirect:/students";
    }

    // 编辑页面
    @GetMapping("/edit/{studentId}")
    public String editForm(@PathVariable String studentId, Model model) {
        Student student = studentService.findById(studentId);
        model.addAttribute("student", student);
        return "edit";
    }

    // 编辑提交
    @PostMapping("/edit")
    public String editSubmit(Student student, RedirectAttributes attributes) {
        studentService.save(student);
        attributes.addFlashAttribute("msg", "修改学生信息成功！");
        attributes.addFlashAttribute("type", "success");
        return "redirect:/students";
    }

    // 单个删除
    @GetMapping("/delete/{studentId}")
    public String delete(@PathVariable String studentId, RedirectAttributes attributes) {
        studentService.deleteById(studentId);
        attributes.addFlashAttribute("msg", "删除学生成功！");
        attributes.addFlashAttribute("type", "success");
        return "redirect:/students";
    }

    // 批量删除
    @PostMapping("/batchDelete")
    public String batchDelete(@RequestParam List<String> studentIds, RedirectAttributes attributes) {
        if (studentIds.isEmpty()) {
            attributes.addFlashAttribute("msg", "请选择要删除的学生！");
            attributes.addFlashAttribute("type", "error");
            return "redirect:/students";
        }
        studentService.batchDelete(studentIds);
        attributes.addFlashAttribute("msg", "批量删除成功！");
        attributes.addFlashAttribute("type", "success");
        return "redirect:/students";
    }
}