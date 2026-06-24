package com.hr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.PageRequest;
import com.hr.common.Result;
import com.hr.entity.BusinessTrip;
import com.hr.entity.Notification;
import com.hr.entity.User;
import com.hr.service.AuthService;
import com.hr.service.BusinessTripService;
import com.hr.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business-trip")
public class BusinessTripController {

    @Autowired
    private BusinessTripService businessTripService;

    @Autowired
    private AuthService authService;
    
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<Page<BusinessTrip>> list(PageRequest pageRequest,
                                            @RequestParam(required = false) Long employeeId,
                                            @RequestParam(required = false) String status) {
        Page<BusinessTrip> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

        QueryWrapper<BusinessTrip> queryWrapper = new QueryWrapper<>();
        if (employeeId != null) {
            queryWrapper.eq("employee_id", employeeId);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByDesc("create_time");
        Page<BusinessTrip> result = businessTripService.page(page, queryWrapper);
        return Result.success(result);
    }

    @GetMapping("/my")
    public Result<Page<BusinessTrip>> getMyList(PageRequest pageRequest,
                                                  @RequestParam(required = false) String status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = authService.getCurrentUser(username);
        if (user == null || user.getEmployeeId() == null) {
            return Result.error("未找到用户关联的员工信息");
        }

        Page<BusinessTrip> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        QueryWrapper<BusinessTrip> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employee_id", user.getEmployeeId());
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");
        Page<BusinessTrip> result = businessTripService.page(page, queryWrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<BusinessTrip> getById(@PathVariable Long id) {
        BusinessTrip businessTrip = businessTripService.getById(id);
        return Result.success(businessTrip);
    }

    @PostMapping
    public Result<String> create(@RequestBody BusinessTrip businessTrip) {
        businessTrip.setStatus("待审批");
        businessTripService.save(businessTrip);
        return Result.success("创建成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> update(@PathVariable Long id, @RequestBody BusinessTrip businessTrip) {
        businessTrip.setId(id);
        businessTripService.updateById(businessTrip);
        return Result.success("更新成功");
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> approve(@PathVariable Long id, @RequestBody BusinessTrip businessTrip) {
        BusinessTrip existing = businessTripService.getById(id);
        existing.setStatus(businessTrip.getStatus());
        existing.setApproveRemark(businessTrip.getApproveRemark());
        businessTripService.updateById(existing);
        
        // 发送通知给员工
        if (existing.getEmployeeId() != null) {
            Notification notification = new Notification();
            notification.setEmployeeId(existing.getEmployeeId());
            notification.setTitle("出差申请审批结果");
            notification.setContent("您的出差申请（目的地：" + existing.getDestination() + "）已" + 
                ("通过".equals(businessTrip.getStatus()) ? "通过" : "被拒绝"));
            notification.setStatus("未读");
            notificationService.saveNotification(notification);
        }
        
        return Result.success("审批成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> delete(@PathVariable Long id) {
        businessTripService.removeById(id);
        return Result.success("删除成功");
    }
}
