package com.hr.service;

import com.hr.entity.Employee;
import com.hr.entity.User;

public interface AuthService {
    String login(String username, String password);
    User getCurrentUser(String username);
    void register(User user);
    void updateCurrentUser(User user);
    Employee getCurrentEmployee(String username);
    void updateEmployeeProfile(Long employeeId, Employee employee);
    void updatePassword(Long userId, String newPassword);
}
