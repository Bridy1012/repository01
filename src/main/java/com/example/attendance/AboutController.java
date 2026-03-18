package com.example.attendance;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AboutController {

    @GetMapping("/about")
    public String about() {
        return "姓名：李佳玉，专业：网络空间安全，班级：24级网络空间安全";
    }
}