package com.hr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.PageRequest;
import com.hr.common.Result;
import com.hr.entity.Notification;
import com.hr.entity.Resignation;
import com.hr.service.NotificationService;
import com.hr.service.ResignationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resignation")
public class ResignationController {

    @Autowired
    private ResignationService resignationService;
    
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public Result<Page<Resignation>> list(PageRequest pageRequest,
                                            @RequestParam(required = false) Long employeeId,
                                            @RequestParam(required = false) String status) {
        Page<Resignation> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

        QueryWrapper<Resignation> queryWrapper = new QueryWrapper<>();
        if (employeeId != null) {
            queryWrapper.eq("employee_id", employeeId);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("approve_status", status);
        }

        queryWrapper.orderByDesc("create_time");
        Page<Resignation> result = resignationService.page(page, queryWrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Resignation> getById(@PathVariable Long id) {
        Resignation resignation = resignationService.getById(id);
        return Result.success(resignation);
    }

    @PostMapping
    public Result<String> create(@RequestBody Resignation resignation) {
        resignation.setApproveStatus("待审批");
        resignationService.save(resignation);
        return Result.success("创建成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> update(@PathVariable Long id, @RequestBody Resignation resignation) {
        resignation.setId(id);
        resignationService.updateById(resignation);
        return Result.success("更新成功");
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> approve(@PathVariable Long id, @RequestBody Resignation resignation) {
        Resignation existing = resignationService.getById(id);
        existing.setApproveStatus(resignation.getApproveStatus());
        existing.setRemark(resignation.getRemark());
        resignationService.updateById(existing);
        
        // 发送通知给员工
        if (existing.getEmployeeId() != null) {
            Notification notification = new Notification();
            notification.setEmployeeId(existing.getEmployeeId());
            notification.setTitle("离职申请审批结果");
            notification.setContent("您的离职申请（日期：" + existing.getResignationDate() + "）已" + 
                ("通过".equals(resignation.getApproveStatus()) ? "通过" : "被拒绝"));
            notification.setStatus("未读");
            notificationService.saveNotification(notification);
        }
        
        return Result.success("审批成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> delete(@PathVariable Long id) {
        resignationService.removeById(id);
        return Result.success("删除成功");
    }
}
