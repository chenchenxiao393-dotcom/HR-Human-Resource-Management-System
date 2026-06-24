package com.hr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.PageRequest;
import com.hr.common.Result;
import com.hr.entity.Notification;
import com.hr.entity.PersonnelChange;
import com.hr.entity.User;
import com.hr.service.AuthService;
import com.hr.service.NotificationService;
import com.hr.service.PersonnelChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personnel-change")
public class PersonnelChangeController {

    @Autowired
    private PersonnelChangeService personnelChangeService;

    @Autowired
    private AuthService authService;
    
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<Page<PersonnelChange>> list(PageRequest pageRequest,
                                               @RequestParam(required = false) Long employeeId,
                                               @RequestParam(required = false) String changeType,
                                               @RequestParam(required = false) String status) {
        Page<PersonnelChange> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

        QueryWrapper<PersonnelChange> queryWrapper = new QueryWrapper<>();
        if (employeeId != null) {
            queryWrapper.eq("employee_id", employeeId);
        }
        if (changeType != null && !changeType.isEmpty()) {
            queryWrapper.eq("change_type", changeType);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByDesc("change_date", "create_time");
        Page<PersonnelChange> result = personnelChangeService.page(page, queryWrapper);
        return Result.success(result);
    }

    @GetMapping("/my")
    public Result<Page<PersonnelChange>> getMyList(PageRequest pageRequest,
                                                      @RequestParam(required = false) String changeType,
                                                      @RequestParam(required = false) String status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = authService.getCurrentUser(username);
        if (user == null || user.getEmployeeId() == null) {
            return Result.error("未找到用户关联的员工信息");
        }

        Page<PersonnelChange> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        QueryWrapper<PersonnelChange> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employee_id", user.getEmployeeId());
        if (changeType != null && !changeType.isEmpty()) {
            queryWrapper.eq("change_type", changeType);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("change_date", "create_time");
        Page<PersonnelChange> result = personnelChangeService.page(page, queryWrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<PersonnelChange> getById(@PathVariable Long id) {
        PersonnelChange personnelChange = personnelChangeService.getById(id);
        return Result.success(personnelChange);
    }

    @PostMapping
    public Result<String> create(@RequestBody PersonnelChange personnelChange) {
        personnelChange.setStatus("待审批");
        personnelChangeService.save(personnelChange);
        return Result.success("创建成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> update(@PathVariable Long id, @RequestBody PersonnelChange personnelChange) {
        PersonnelChange existing = personnelChangeService.getById(id);
        PersonnelChange updated = new PersonnelChange();
        updated.setId(id);
        
        // 记录变更内容
        String changeContent = "";
        if (!existing.getChangeType().equals(personnelChange.getChangeType())) {
            changeContent += "变动类型从【" + existing.getChangeType() + "】变为【" + personnelChange.getChangeType() + "】；";
        }
        if (!existing.getNewValue().equals(personnelChange.getNewValue())) {
            changeContent += "新值从【" + existing.getNewValue() + "】变为【" + personnelChange.getNewValue() + "】；";
        }
        
        personnelChangeService.updateById(updated);
        
        // 发送通知给员工
        if (existing.getEmployeeId() != null) {
            Notification notification = new Notification();
            notification.setEmployeeId(existing.getEmployeeId());
            notification.setTitle("人事变动通知");
            notification.setContent("您的人事信息发生了变动：" + changeContent);
            notification.setStatus("未读");
            notificationService.saveNotification(notification);
        }
        
        return Result.success("更新成功");
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> approve(@PathVariable Long id, @RequestBody PersonnelChange personnelChange) {
        PersonnelChange existing = personnelChangeService.getById(id);
        existing.setStatus(personnelChange.getStatus());
        personnelChangeService.updateById(existing);
        
        // 发送通知给员工
        if (existing.getEmployeeId() != null) {
            Notification notification = new Notification();
            notification.setEmployeeId(existing.getEmployeeId());
            notification.setTitle("人事变动审批结果");
            notification.setContent("您提交的人事变动申请已" + 
                ("通过".equals(personnelChange.getStatus()) ? "通过" : "被拒绝"));
            notification.setStatus("未读");
            notificationService.saveNotification(notification);
        }
        
        return Result.success("审批成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> delete(@PathVariable Long id) {
        personnelChangeService.removeById(id);
        return Result.success("删除成功");
    }
}
