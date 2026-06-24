package com.hr.service.impl;

import com.hr.entity.Notification;
import com.hr.mapper.NotificationMapper;
import com.hr.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public List<Notification> getByEmployeeId(Long employeeId) {
        return notificationMapper.selectByEmployeeId(employeeId);
    }

    @Override
    public void saveNotification(Notification notification) {
        notificationMapper.insert(notification);
    }

    @Override
    public void markAsRead(Long id) {
        Notification notification = notificationMapper.selectById(id);
        if (notification != null) {
            notification.setStatus("已读");
            notificationMapper.updateById(notification);
        }
    }
    
    @Override
    public int getUnreadCount(Long employeeId) {
        List<Notification> notifications = notificationMapper.selectByEmployeeId(employeeId);
        return (int) notifications.stream().filter(n -> "未读".equals(n.getStatus())).count();
    }
    
    @Override
    public List<Notification> getUnreadNotifications(Long employeeId) {
        List<Notification> notifications = notificationMapper.selectByEmployeeId(employeeId);
        return notifications.stream().filter(n -> "未读".equals(n.getStatus())).toList();
    }

    @Override
    public List<Notification> listAll() {
        return notificationMapper.selectList(null);
    }
}