package com.example.attendance.controller;

import com.example.attendance.Student;
import com.example.attendance.User;
import com.example.attendance.repository.AttendanceRecordRepository;
import com.example.attendance.repository.LeaveApplicationRepository;
import com.example.attendance.repository.UserRepository;
import com.example.attendance.service.StudentService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    // 学生列表（带统计）
    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
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

        if (!students.isEmpty()) {
            List<String> studentIds = students.stream().map(Student::getStudentId).collect(Collectors.toList());
            Map<String, Long> checkInCountMap = attendanceRecordRepository.findByStudentIdIn(studentIds).stream()
                    .collect(Collectors.groupingBy(com.example.attendance.AttendanceRecord::getStudentId, Collectors.counting()));
            Map<String, Long> lateCountMap = attendanceRecordRepository.findByStudentIdInAndStatus(studentIds, "迟到").stream()
                    .collect(Collectors.groupingBy(com.example.attendance.AttendanceRecord::getStudentId, Collectors.counting()));
            Map<String, Long> leaveCountMap = leaveApplicationRepository.findByStudentIdInAndStatus(studentIds, "approved").stream()
                    .collect(Collectors.groupingBy(com.example.attendance.LeaveApplication::getStudentId, Collectors.counting()));

            model.addAttribute("checkInCountMap", checkInCountMap);
            model.addAttribute("lateCountMap", lateCountMap);
            model.addAttribute("leaveCountMap", leaveCountMap);
        }

        model.addAttribute("students", students);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("student", new Student());
        return "add";
    }

    @PostMapping("/add")
    public String addSubmit(Student student, RedirectAttributes attributes) {
        if (studentService.existsByStudentId(student.getStudentId())) {
            attributes.addFlashAttribute("msg", "新增失败：学号已存在！");
            attributes.addFlashAttribute("type", "error");
            return "redirect:/students/add";
        }
        if (userRepository.findByUsername(student.getStudentId()) != null) {
            attributes.addFlashAttribute("msg", "新增失败：该学号对应的用户账号已存在！");
            attributes.addFlashAttribute("type", "error");
            return "redirect:/students/add";
        }
        studentService.save(student);
        // 同步创建 user 账号
        User user = new User();
        user.setUsername(student.getStudentId());
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRole("student");
        user.setName(student.getName());
        userRepository.save(user);
        attributes.addFlashAttribute("msg", "新增学生成功！默认密码：123456");
        attributes.addFlashAttribute("type", "success");
        return "redirect:/students";
    }

    @GetMapping("/edit/{studentId}")
    public String editForm(@PathVariable String studentId, Model model) {
        Student student = studentService.findById(studentId);
        model.addAttribute("student", student);
        return "edit";
    }

    @PostMapping("/edit")
    public String editSubmit(Student student, RedirectAttributes attributes) {
        Student oldStudent = studentService.findById(student.getStudentId());
        studentService.save(student);
        // 如果姓名变化，同步更新 user 表的 name
        if (!oldStudent.getName().equals(student.getName())) {
            User user = userRepository.findByUsername(student.getStudentId());
            if (user != null) {
                user.setName(student.getName());
                userRepository.save(user);
            }
        }
        attributes.addFlashAttribute("msg", "修改学生信息成功！");
        attributes.addFlashAttribute("type", "success");
        return "redirect:/students";
    }

    // 单个删除：同时删除 student 和 user
    @GetMapping("/delete/{studentId}")
    public String delete(@PathVariable String studentId, RedirectAttributes attributes) {
        // 删除关联的考勤记录和请假记录（外键级联或手动）
        attendanceRecordRepository.deleteAll(attendanceRecordRepository.findByStudentId(studentId));
        leaveApplicationRepository.deleteAll(leaveApplicationRepository.findByStudentIdOrderByApplyTimeDesc(studentId));
        // 删除 student
        studentService.deleteById(studentId);
        // 删除对应的 user
        User user = userRepository.findByUsername(studentId);
        if (user != null) {
            userRepository.delete(user);
        }
        attributes.addFlashAttribute("msg", "删除学生成功！已同步删除登录账号。");
        attributes.addFlashAttribute("type", "success");
        return "redirect:/students";
    }

    // 批量删除：同时删除 student 和对应的 user
    @PostMapping("/batchDelete")
    public String batchDelete(@RequestParam List<String> studentIds, RedirectAttributes attributes) {
        if (studentIds.isEmpty()) {
            attributes.addFlashAttribute("msg", "请选择要删除的学生！");
            attributes.addFlashAttribute("type", "error");
            return "redirect:/students";
        }
        for (String studentId : studentIds) {
            // 删除关联记录
            attendanceRecordRepository.deleteAll(attendanceRecordRepository.findByStudentId(studentId));
            leaveApplicationRepository.deleteAll(leaveApplicationRepository.findByStudentIdOrderByApplyTimeDesc(studentId));
            // 删除 student
            studentService.deleteById(studentId);
            // 删除对应的 user
            User user = userRepository.findByUsername(studentId);
            if (user != null) {
                userRepository.delete(user);
            }
        }
        attributes.addFlashAttribute("msg", "批量删除成功！已同步删除登录账号。");
        attributes.addFlashAttribute("type", "success");
        return "redirect:/students";
    }

    // 批量导入页面
    @GetMapping("/import")
    public String importPage() {
        return "student-import";
    }

    @PostMapping("/import")
    public String importStudents(@RequestParam("file") MultipartFile file, RedirectAttributes ra) {
        if (file.isEmpty()) {
            ra.addFlashAttribute("msg", "请选择要上传的文件");
            ra.addFlashAttribute("type", "error");
            return "redirect:/students/import";
        }
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            List<Student> studentList = new ArrayList<>();
            List<User> userList = new ArrayList<>();
            int successCount = 0, failCount = 0;
            StringBuilder failDetails = new StringBuilder();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                try {
                    String studentId = getCellStringValue(row.getCell(0));
                    String name = getCellStringValue(row.getCell(1));
                    String gender = getCellStringValue(row.getCell(2));
                    String className = getCellStringValue(row.getCell(3));
                    String birthDateStr = getCellStringValue(row.getCell(4));
                    String phone = getCellStringValue(row.getCell(5));

                    if (studentId == null || studentId.trim().isEmpty() || name == null || name.trim().isEmpty()) {
                        failCount++;
                        failDetails.append("第").append(i + 1).append("行：学号或姓名为空<br>");
                        continue;
                    }
                    if (studentService.existsByStudentId(studentId) || userRepository.findByUsername(studentId) != null) {
                        failCount++;
                        failDetails.append("第").append(i + 1).append("行：学号 ").append(studentId).append(" 已存在<br>");
                        continue;
                    }

                    LocalDate birthDate = null;
                    if (birthDateStr != null && !birthDateStr.isEmpty()) {
                        try {
                            birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        } catch (Exception e) {
                            try {
                                birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                            } catch (Exception ex) {
                                failCount++;
                                failDetails.append("第").append(i + 1).append("行：出生日期格式错误<br>");
                                continue;
                            }
                        }
                    }

                    Student student = new Student();
                    student.setStudentId(studentId);
                    student.setName(name);
                    student.setGender(gender);
                    student.setClassName(className);
                    student.setBirthDate(birthDate);
                    student.setPhone(phone);
                    student.setAttendanceCount(0);
                    studentList.add(student);

                    User user = new User();
                    user.setUsername(studentId);
                    user.setPassword(passwordEncoder.encode("123456"));
                    user.setRole("student");
                    user.setName(name);
                    userList.add(user);

                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failDetails.append("第").append(i + 1).append("行：").append(e.getMessage()).append("<br>");
                }
            }

            if (!studentList.isEmpty()) studentService.batchSave(studentList);
            if (!userList.isEmpty()) userRepository.saveAll(userList);

            String msg = String.format("批量导入完成：成功 %d 条，失败 %d 条", successCount, failCount);
            if (failCount > 0) msg += "<br>失败明细：<br>" + failDetails.toString();
            ra.addFlashAttribute("msg", msg);
            ra.addFlashAttribute("type", failCount > 0 ? "error" : "success");
        } catch (Exception e) {
            ra.addFlashAttribute("msg", "导入失败：" + e.getMessage());
            ra.addFlashAttribute("type", "error");
        }
        return "redirect:/students/import";
    }

    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=student_template.xlsx");
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("学生信息");
            Row header = sheet.createRow(0);
            String[] headers = {"学号", "姓名", "性别", "班级", "出生日期", "联系方式"};
            for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);
            workbook.write(response.getOutputStream());
        }
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            default: return null;
        }
    }
}