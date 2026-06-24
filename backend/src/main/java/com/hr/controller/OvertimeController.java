package com.hr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.PageRequest;
import com.hr.common.Result;
import com.hr.entity.Notification;
import com.hr.entity.Overtime;
import com.hr.entity.User;
import com.hr.service.AuthService;
import com.hr.service.NotificationService;
import com.hr.service.OvertimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/overtime")
public class OvertimeController {

    @Autowired
    private OvertimeService overtimeService;

    @Autowired
    private AuthService authService;
    
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<Page<Overtime>> list(PageRequest pageRequest,
                                        @RequestParam(required = false) Long employeeId,
                                        @RequestParam(required = false) String status) {
        Page<Overtime> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

        QueryWrapper<Overtime> queryWrapper = new QueryWrapper<>();
        if (employeeId != null) {
            queryWrapper.eq("employee_id", employeeId);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByDesc("create_time");
        Page<Overtime> result = overtimeService.page(page, queryWrapper);
        return Result.success(result);
    }

    @GetMapping("/my")
    public Result<Page<Overtime>> getMyList(PageRequest pageRequest,
                                             @RequestParam(required = false) String status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = authService.getCurrentUser(username);
        if (user == null || user.getEmployeeId() == null) {
            return Result.error("未找到用户关联的员工信息");
        }

        Page<Overtime> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        QueryWrapper<Overtime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employee_id", user.getEmployeeId());
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");
        Page<Overtime> result = overtimeService.page(page, queryWrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Overtime> getById(@PathVariable Long id) {
        Overtime overtime = overtimeService.getById(id);
        return Result.success(overtime);
    }

    @PostMapping
    public Result<String> create(@RequestBody Overtime overtime) {
        overtime.setStatus("待审批");
        overtimeService.save(overtime);
        return Result.success("创建成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> update(@PathVariable Long id, @RequestBody Overtime overtime) {
        overtime.setId(id);
        overtimeService.updateById(overtime);
        return Result.success("更新成功");
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> approve(@PathVariable Long id, @RequestBody Overtime overtime) {
        Overtime existing = overtimeService.getById(id);
        existing.setStatus(overtime.getStatus());
        existing.setApproveRemark(overtime.getApproveRemark());
        overtimeService.updateById(existing);
        
        // 发送通知给员工
        if (existing.getEmployeeId() != null) {
            Notification notification = new Notification();
            notification.setEmployeeId(existing.getEmployeeId());
            notification.setTitle("加班申请审批结果");
            notification.setContent("您的加班申请（日期：" + existing.getOvertimeDate() + "）已" + 
                ("通过".equals(overtime.getStatus()) ? "通过" : "被拒绝"));
            notification.setStatus("未读");
            notificationService.saveNotification(notification);
        }
        
        return Result.success("审批成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> delete(@PathVariable Long id) {
        overtimeService.removeById(id);
        return Result.success("删除成功");
    }
}
