package com.hr.controller;

import com.hr.common.Result;
import com.hr.entity.Notification;
import com.hr.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public Result<List<Notification>> getNotifications(@RequestParam Long employeeId) {
        List<Notification> notifications = notificationService.getByEmployeeId(employeeId);
        return Result.success(notifications);
    }

    @GetMapping("/unread")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public Result<List<Notification>> getUnreadNotifications(@RequestParam Long employeeId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(employeeId);
        return Result.success(notifications);
    }

    @GetMapping("/unread/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public Result<Integer> getUnreadCount(@RequestParam Long employeeId) {
        int count = notificationService.getUnreadCount(employeeId);
        return Result.success(count);
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public Result<String> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return Result.success("已标记为已读");
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.listAll();
        return Result.success(notifications);
    }

    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> sendNotification(@RequestBody Map<String, Object> data) {
        Long employeeId = Long.valueOf(data.get("employeeId").toString());
        String title = (String) data.get("title");
        String content = (String) data.get("content");
        String type = (String) data.get("type");

        Notification notification = new Notification();
        notification.setEmployeeId(employeeId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type != null ? type : "系统通知");
        notification.setStatus("未读");
        notificationService.saveNotification(notification);
        return Result.success("发送成功");
    }
}