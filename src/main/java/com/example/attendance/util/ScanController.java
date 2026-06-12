package com.example.attendance.controller;

import com.example.attendance.Course;
import com.example.attendance.service.CourseService;
import com.example.attendance.util.QRCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@Controller
public class ScanController {

    @Autowired
    private CourseService courseService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final String SECRET_KEY = "YourSecretKeyForQRCodeSign2024!";
    private static final long QR_EXPIRE_MINUTES = 5;

    @GetMapping("/course/qrcode/{courseId}")
    public String showQRCode(@PathVariable Long courseId, Model model) {
        Course course = courseService.findById(courseId).orElse(null);
        if (course == null) {
            return "error/404";
        }

        long timestamp = Instant.now().getEpochSecond() / 60;
        String sign = generateSign(courseId, timestamp);
        String qrContent = String.format("%s/scan/signin?courseId=%d&t=%d&sign=%s",
                baseUrl, courseId, timestamp, sign);
        String qrBase64 = QRCodeUtil.generateQRCodeBase64(qrContent, 300, 300);

        model.addAttribute("qrBase64", qrBase64);
        model.addAttribute("course", course);
        return "qrcode-show";
    }

    @GetMapping("/scan/signin")
    public String scanSignin(@RequestParam Long courseId,
                             @RequestParam long t,
                             @RequestParam String sign,
                             Model model) {
        // 验证签名
        String expectedSign = generateSign(courseId, t);
        if (!expectedSign.equals(sign)) {
            model.addAttribute("msg", "无效的签到二维码");
            return "error/error-page";
        }
        // 验证有效期
        long currentMinute = Instant.now().getEpochSecond() / 60;
        if (currentMinute - t > QR_EXPIRE_MINUTES) {
            model.addAttribute("msg", "二维码已过期");
            return "error/error-page";
        }
        // 验证课程是否存在
        Course course = courseService.findById(courseId).orElse(null);
        if (course == null) {
            model.addAttribute("msg", "课程不存在");
            return "error/error-page";
        }
        // 重定向到登录页，携带课程ID
        return "redirect:/login?courseId=" + courseId;
    }

    private String generateSign(Long courseId, long timestamp) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            String data = courseId + ":" + timestamp;
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return "";
        }
    }
}