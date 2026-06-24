package com.hr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.PageRequest;
import com.hr.common.Result;
import com.hr.entity.User;
import com.hr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<User>> list(PageRequest pageRequest,
                                    @RequestParam(required = false) String username,
                                    @RequestParam(required = false) String role,
                                    @RequestParam(required = false) String status) {
        Page<User> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            queryWrapper.like("username", username);
        }
        if (role != null && !role.isEmpty()) {
            queryWrapper.eq("role", role);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByDesc("create_time");
        Page<User> result = userService.page(page, queryWrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> create(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus("NORMAL");
        userService.save(user);
        return Result.success("创建成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> update(@PathVariable Long id, @RequestBody User user) {
        User existing = userService.getById(id);
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        existing.setRealName(user.getRealName());
        existing.setRole(user.getRole());
        existing.setEmployeeId(user.getEmployeeId());
        existing.setStatus(user.getStatus());
        userService.updateById(existing);
        return Result.success("更新成功");
    }

    @PutMapping("/{id}/password")
    public Result<String> changePassword(@PathVariable Long id, @RequestBody User user) {
        User existing = userService.getById(id);
        existing.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.updateById(existing);
        return Result.success("密码修改成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success("删除成功");
    }
}
