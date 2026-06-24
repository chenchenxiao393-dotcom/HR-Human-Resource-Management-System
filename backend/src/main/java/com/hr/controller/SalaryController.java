package com.hr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hr.common.Result;
import com.hr.entity.Employee;
import com.hr.entity.Overtime;
import com.hr.entity.Salary;
import com.hr.entity.User;
import com.hr.mapper.EmployeeMapper;
import com.hr.mapper.OvertimeMapper;
import com.hr.mapper.UserMapper;
import com.hr.security.JwtTokenUtil;
import com.hr.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/salary")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private OvertimeMapper overtimeMapper;

    private Long getCurrentEmployeeId(String authHeader) {
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(token);
            } catch (Exception e) {
                return null;
            }
        }
        if (username == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                username = auth.getName();
            }
        }
        if (username == null) {
            return null;
        }
        User user = userMapper.selectOne(
            new QueryWrapper<User>().eq("username", username).eq("deleted", 0)
        );
        return user != null ? user.getEmployeeId() : null;
    }

    @GetMapping("/my")
    public Result<Map<String, Object>> getMySalary(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long employeeId = getCurrentEmployeeId(authHeader);
        if (employeeId == null) {
            return Result.error("无法获取用户信息");
        }

        // 获取员工基本信息（包含薪资）
        Employee employee = employeeMapper.selectById(employeeId);

        // 获取当前月的加班记录（只统计已审批通过的）
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        QueryWrapper<Overtime> overtimeQuery = new QueryWrapper<>();
        overtimeQuery.eq("employee_id", employeeId);
        overtimeQuery.eq("status", "通过");
        overtimeQuery.ge("overtime_date", startDate);
        overtimeQuery.le("overtime_date", endDate);
        overtimeQuery.orderByDesc("overtime_date");
        List<Overtime> overtimeList = overtimeMapper.selectList(overtimeQuery);

        // 计算加班总时长
        double totalOvertimeHours = overtimeList.stream()
            .filter(o -> o.getHours() != null)
            .mapToDouble(Overtime::getHours)
            .sum();

        // 构建返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("employeeId", employeeId);
        result.put("employeeName", employee != null ? employee.getName() : "");
        result.put("salary", employee != null ? employee.getSalary() : BigDecimal.ZERO);
        result.put("salaryMonth", currentMonth.toString());
        result.put("overtimeHours", totalOvertimeHours);
        result.put("overtimeList", overtimeList);

        return Result.success(result);
    }

    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getSalaryHistory(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long employeeId = getCurrentEmployeeId(authHeader);
        if (employeeId == null) {
            return Result.error("无法获取用户信息");
        }

        // 获取最近6个月的加班记录
        YearMonth currentMonth = YearMonth.now();
        List<Map<String, Object>> history = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            YearMonth month = currentMonth.minusMonths(i);
            LocalDate startDate = month.atDay(1);
            LocalDate endDate = month.atEndOfMonth();

            QueryWrapper<Overtime> overtimeQuery = new QueryWrapper<>();
            overtimeQuery.eq("employee_id", employeeId);
            overtimeQuery.eq("status", "通过");
            overtimeQuery.ge("overtime_date", startDate);
            overtimeQuery.le("overtime_date", endDate);
            List<Overtime> overtimeList = overtimeMapper.selectList(overtimeQuery);

            double totalHours = overtimeList.stream()
                .filter(o -> o.getHours() != null)
                .mapToDouble(Overtime::getHours)
                .sum();

            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", month.toString());
            monthData.put("overtimeHours", totalHours);
            monthData.put("overtimeCount", overtimeList.size());
            history.add(monthData);
        }

        return Result.success(history);
    }
}
