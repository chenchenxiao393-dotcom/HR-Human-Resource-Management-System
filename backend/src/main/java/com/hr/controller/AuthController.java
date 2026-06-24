package com.hr.controller;

import com.hr.common.Result;
import com.hr.entity.Employee;
import com.hr.entity.User;
import com.hr.security.JwtTokenUtil;
import com.hr.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        String token = authService.login(username, password);
        User user = authService.getCurrentUser(username);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        data.put("needChangePassword", user.getNeedChangePassword() != null && user.getNeedChangePassword());
        return Result.success(data);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        authService.register(user);
        return Result.success("注册成功");
    }

    @GetMapping("/current")
    public Result<User> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(token);
            } catch (Exception e) {
                return Result.error("Token无效");
            }
        }
        if (username == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                username = auth.getName();
            }
        }
        if (username == null) {
            return Result.error("未登录");
        }
        User user = authService.getCurrentUser(username);
        return Result.success(user);
    }

    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody User user) {
        authService.updateCurrentUser(user);
        return Result.success("更新成功");
    }

    @GetMapping("/employee-profile")
    public Result<Employee> getEmployeeProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(token);
            } catch (Exception e) {
                return Result.error("Token无效");
            }
        }
        if (username == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                username = auth.getName();
            }
        }
        if (username == null) {
            return Result.error("未登录");
        }
        Employee employee = authService.getCurrentEmployee(username);
        return Result.success(employee);
    }

    @PutMapping("/employee-profile")
    public Result<String> updateEmployeeProfile(@RequestBody Employee employee) {
        authService.updateEmployeeProfile(employee.getId(), employee);
        return Result.success("更新成功");
    }
    
    @PutMapping("/change-password")
    public Result<String> changePassword(@RequestBody Map<String, String> passwordData,
                                        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(token);
            } catch (Exception e) {
                return Result.error("Token无效");
            }
        }
        if (username == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                username = auth.getName();
            }
        }
        if (username == null) {
            return Result.error("未登录");
        }
        
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        String confirmPassword = passwordData.get("confirmPassword");
        
        // 验证新密码长度
        if (newPassword == null || newPassword.length() < 6) {
            return Result.error("新密码长度不能少于6位");
        }
        
        // 验证新密码和确认密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            return Result.error("两次输入的新密码不一致");
        }
        
        // 验证原密码
        User user = authService.getCurrentUser(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 验证原密码是否正确
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("原密码错误");
        }
        
        // 更新密码
        authService.updatePassword(user.getId(), newPassword);
        return Result.success("密码修改成功，请重新登录");
    }
}
