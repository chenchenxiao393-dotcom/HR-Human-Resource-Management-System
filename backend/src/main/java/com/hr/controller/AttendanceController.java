package com.hr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.PageRequest;
import com.hr.common.Result;
import com.hr.entity.Attendance;
import com.hr.entity.User;
import com.hr.service.AttendanceService;
import com.hr.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AuthService authService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<Page<Attendance>> list(PageRequest pageRequest,
                                          @RequestParam(required = false) Long employeeId,
                                          @RequestParam(required = false) String startDate,
                                          @RequestParam(required = false) String endDate,
                                          @RequestParam(required = false) String status) {
        Page<Attendance> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

        QueryWrapper<Attendance> queryWrapper = new QueryWrapper<>();
        if (employeeId != null) {
            queryWrapper.eq("employee_id", employeeId);
        }
        if (startDate != null && !startDate.isEmpty()) {
            queryWrapper.ge("attendance_date", startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            queryWrapper.le("attendance_date", endDate);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByDesc("attendance_date", "create_time");
        Page<Attendance> result = attendanceService.page(page, queryWrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Attendance> getById(@PathVariable Long id) {
        Attendance attendance = attendanceService.getById(id);
        return Result.success(attendance);
    }

    @PostMapping
    public Result<String> create(@RequestBody Attendance attendance) {
        attendanceService.save(attendance);
        return Result.success("创建成功");
    }

    @PostMapping("/check-in")
    public Result<String> checkIn(@RequestBody Map<String, Object> data) {
        Long employeeId = Long.valueOf(data.get("employeeId").toString());
        
        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setAttendanceDate(LocalDate.now());
        attendance.setCheckInTime(data.get("checkInTime").toString());
        attendance.setStatus("正常");
        attendanceService.save(attendance);
        return Result.success("签到成功");
    }

    @PostMapping("/check-out")
    public Result<String> checkOut(@RequestBody Map<String, Object> data) {
        Long employeeId = Long.valueOf(data.get("employeeId").toString());
        
        QueryWrapper<Attendance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employee_id", employeeId);
        queryWrapper.eq("attendance_date", LocalDate.now());
        queryWrapper.and(wrapper -> wrapper.isNull("check_out_time").or().apply("check_out_time = ''"));
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("LIMIT 1");
        
        Attendance attendance = attendanceService.getOne(queryWrapper);
        
        if (attendance != null) {
            attendance.setCheckOutTime(data.get("checkOutTime").toString());
            attendanceService.updateById(attendance);
            return Result.success("签退成功");
        }
        return Result.error("未找到可签退的记录");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> update(@PathVariable Long id, @RequestBody Attendance attendance) {
        attendance.setId(id);
        attendanceService.updateById(attendance);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> delete(@PathVariable Long id) {
        attendanceService.removeById(id);
        return Result.success("删除成功");
    }

    @GetMapping("/my")
    public Result<Page<Attendance>> getMyAttendance(PageRequest pageRequest,
                                                    @RequestParam(required = false) String startDate,
                                                    @RequestParam(required = false) String endDate,
                                                    @RequestParam(required = false) String status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = authService.getCurrentUser(username);
        if (user == null || user.getEmployeeId() == null) {
            return Result.error("未找到用户关联的员工信息");
        }

        Page<Attendance> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        QueryWrapper<Attendance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employee_id", user.getEmployeeId());
        if (startDate != null && !startDate.isEmpty()) {
            queryWrapper.ge("attendance_date", startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            queryWrapper.le("attendance_date", endDate);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("attendance_date", "create_time");
        Page<Attendance> result = attendanceService.page(page, queryWrapper);
        return Result.success(result);
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<List<Attendance>> exportAttendance(@RequestParam(required = false) Long employeeId,
                                                     @RequestParam(required = false) String startDate,
                                                     @RequestParam(required = false) String endDate,
                                                     @RequestParam(required = false) String status) {
        QueryWrapper<Attendance> queryWrapper = new QueryWrapper<>();
        if (employeeId != null) {
            queryWrapper.eq("employee_id", employeeId);
        }
        if (startDate != null && !startDate.isEmpty()) {
            queryWrapper.ge("attendance_date", startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            queryWrapper.le("attendance_date", endDate);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("attendance_date", "create_time");
        List<Attendance> list = attendanceService.list(queryWrapper);
        return Result.success(list);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(@RequestParam String startDate,
                                                      @RequestParam String endDate) {
        QueryWrapper<Attendance> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("attendance_date", startDate);
        queryWrapper.le("attendance_date", endDate);

        List<Attendance> list = attendanceService.list(queryWrapper);

        long normal = list.stream().filter(a -> "正常".equals(a.getStatus())).count();
        long late = list.stream().filter(a -> "迟到".equals(a.getStatus())).count();
        long early = list.stream().filter(a -> "早退".equals(a.getStatus())).count();
        long absent = list.stream().filter(a -> "缺勤".equals(a.getStatus())).count();

        long checkedInCount = list.stream()
            .filter(a -> a.getCheckInTime() != null && !a.getCheckInTime().isEmpty())
            .map(Attendance::getEmployeeId)
            .distinct()
            .count();

        return Result.success(Map.of(
            "total", list.size(),
            "normal", normal,
            "late", late,
            "early", early,
            "absent", absent,
            "checkedInCount", checkedInCount
        ));
    }
}
