package com.hr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hr.entity.Employee;
import com.hr.entity.User;
import com.hr.mapper.EmployeeMapper;
import com.hr.mapper.UserMapper;
import com.hr.security.JwtTokenUtil;
import com.hr.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public String login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
        User user = userMapper.selectOne(
            new QueryWrapper<User>().eq("username", username).eq("deleted", 0)
        );
        return jwtTokenUtil.generateToken(username, user.getRole());
    }

    @Override
    public User getCurrentUser(String username) {
        return userMapper.selectOne(
            new QueryWrapper<User>().eq("username", username).eq("deleted", 0)
        );
    }

    @Override
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus("NORMAL");
        userMapper.insert(user);
    }

    @Override
    public void updateCurrentUser(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername()).eq("deleted", 0);
        User existing = userMapper.selectOne(queryWrapper);
        if (existing != null) {
            String oldRealName = existing.getRealName();
            String newRealName = user.getRealName();
            existing.setRealName(newRealName);
            userMapper.updateById(existing);
            
            // 同步更新 employee 表的 name 字段
            if (existing.getEmployeeId() != null && newRealName != null && !newRealName.equals(oldRealName)) {
                Employee employee = employeeMapper.selectById(existing.getEmployeeId());
                if (employee != null) {
                    employee.setName(newRealName);
                    employeeMapper.updateById(employee);
                }
            }
        }
    }

    @Override
    public Employee getCurrentEmployee(String username) {
        User user = userMapper.selectOne(
            new QueryWrapper<User>().eq("username", username).eq("deleted", 0)
        );
        if (user != null && user.getEmployeeId() != null) {
            return employeeMapper.selectById(user.getEmployeeId());
        }
        return null;
    }

    @Override
    public void updateEmployeeProfile(Long employeeId, Employee employee) {
        Employee existing = employeeMapper.selectById(employeeId);
        if (existing != null) {
            if (employee.getBirthDate() != null) {
                existing.setBirthDate(employee.getBirthDate());
            }
            if (employee.getPhone() != null) {
                existing.setPhone(employee.getPhone());
            }
            if (employee.getEmail() != null) {
                existing.setEmail(employee.getEmail());
            }
            if (employee.getAddress() != null) {
                existing.setAddress(employee.getAddress());
            }
            employeeMapper.updateById(existing);
        }
    }
    
    @Override
    public void updatePassword(Long userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setNeedChangePassword(false);
            userMapper.updateById(user);
        }
    }
}
