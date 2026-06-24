package com.hr.service;

import com.hr.entity.Notification;

import java.util.List;

public interface NotificationService {

    List<Notification> getByEmployeeId(Long employeeId);
    
    void saveNotification(Notification notification);
    
    void markAsRead(Long id);
    
    int getUnreadCount(Long employeeId);
    
    List<Notification> getUnreadNotifications(Long employeeId);

    List<Notification> listAll();
}