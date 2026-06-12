package com.example.attendance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler successHandler() {
        return new SavedRequestAwareAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws ServletException, IOException {
                // 获取登录请求中携带的 courseId 参数
                String courseIdParam = request.getParameter("courseId");
                if (courseIdParam != null && !courseIdParam.isEmpty()) {
                    try {
                        Long courseId = Long.valueOf(courseIdParam);
                        HttpSession session = request.getSession();
                        session.setAttribute("scanCourseId", courseId);
                    } catch (NumberFormatException e) {
                        // 忽略无效ID
                    }
                }

                String role = authentication.getAuthorities().iterator().next().getAuthority();
                if ("teacher".equals(role)) {
                    getRedirectStrategy().sendRedirect(request, response, "/students");
                } else if ("student".equals(role)) {
                    getRedirectStrategy().sendRedirect(request, response, "/attendance");
                } else {
                    getRedirectStrategy().sendRedirect(request, response, "/login");
                }
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/css/**", "/js/**", "/scan/**").permitAll()
                        .requestMatchers("/students/**").hasAuthority("teacher")
                        .requestMatchers("/courses/**").hasAuthority("teacher")
                        .requestMatchers("/attendance/list", "/attendance/export", "/attendance/delete/**", "/attendance/batchDelete").hasAuthority("teacher")
                        .requestMatchers("/attendance", "/attendance/checkin", "/attendance/checkout").hasAuthority("student")
                        .requestMatchers("/attendance/list").hasAnyAuthority("student", "teacher")
                        .requestMatchers("/leave/apply", "/leave/my").hasAuthority("student")
                        .requestMatchers("/leave/pending", "/leave/approve", "/leave/reject").hasAuthority("teacher")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler())
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );
        return http.build();
    }
}