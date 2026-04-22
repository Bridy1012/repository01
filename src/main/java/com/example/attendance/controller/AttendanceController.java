package com.example.attendance.controller;

import com.example.attendance.Attendance;
import com.example.attendance.Result;
import com.example.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // 原有CRUD接口不变
    @PostMapping("/save")
    public Result<Attendance> saveAttendance(@RequestBody Attendance attendance) {
        Attendance save = attendanceService.saveAttendance(attendance);
        return Result.success(save);
    }

    @GetMapping("/{id}")
    public Result<Attendance> getAttendanceById(@PathVariable Long id) {
        Optional<Attendance> attendance = attendanceService.getAttendanceById(id);
        return attendance.map(Result::success).orElseGet(() -> Result.error(404, "考勤记录不存在"));
    }

    @GetMapping("/list")
    public Result<List<Attendance>> getAllAttendances() {
        List<Attendance> list = attendanceService.getAllAttendances();
        return Result.success(list);
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return Result.success("考勤记录删除成功");
    }

    // 新增：分页+排序+多条件查询接口
    @GetMapping("/page")
    public Result<Page<Attendance>> pageAttendances(
            // 分页参数（默认第1页，每页10条）
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,

            // 排序参数（默认按考勤日期降序）
            @RequestParam(defaultValue = "attendanceDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,

            // 多条件筛选参数（全部可选）
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String status
    ) {
        // 构建排序对象
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        // 构建分页对象（页码从0开始，对应前端的第1页）
        PageRequest pageable = PageRequest.of(page, size, sort);

        // 调用服务层方法
        Page<Attendance> pageResult = attendanceService.pageAttendances(studentId, startDate, endDate, status, pageable);

        return Result.success(pageResult);
    }
}