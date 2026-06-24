package com.hr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.PageRequest;
import com.hr.common.Result;
import com.hr.entity.Notification;
import com.hr.entity.Task;
import com.hr.entity.User;
import com.hr.mapper.UserMapper;
import com.hr.security.JwtTokenUtil;
import com.hr.service.NotificationService;
import com.hr.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private Long getCurrentEmployeeId(String authHeader) {
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(token);
            } catch (Exception e) {
                return null;
            }
        }
        if (username == null) {
            return null;
        }
        User user = userMapper.selectOne(
            new QueryWrapper<User>().eq("username", username).eq("deleted", 0)
        );
        return user != null ? user.getEmployeeId() : null;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<Map<String, Object>> list(PageRequest pageRequest,
                                   @RequestParam(required = false) String taskName,
                                   @RequestParam(required = false) Long employeeId,
                                   @RequestParam(required = false) String status) {
        Page<Task> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        if (taskName != null && !taskName.isEmpty()) {
            queryWrapper.like("task_name", taskName);
        }
        if (employeeId != null) {
            queryWrapper.eq("employee_id", employeeId);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByDesc("create_time");
        Page<Task> result = taskService.page(page, queryWrapper);
        
        List<Map<String, Object>> records = result.getRecords().stream()
            .map(task -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", task.getId());
                map.put("taskName", task.getTaskName());
                map.put("description", task.getDescription() != null ? task.getDescription() : "");
                map.put("employeeId", task.getEmployeeId());
                map.put("employeeName", task.getEmployeeName());
                map.put("deadline", task.getDeadline() != null ? task.getDeadline().toString() : "");
                map.put("status", task.getStatus());
                map.put("progress", task.getProgress());
                map.put("priority", task.getPriority());
                map.put("creator", task.getCreator());
                map.put("createTime", task.getCreateTime());
                map.put("updateTime", task.getUpdateTime());
                return map;
            })
            .toList();
        
        Map<String, Object> pageData = new HashMap<>();
        pageData.put("records", records);
        pageData.put("total", result.getTotal());
        pageData.put("pages", result.getPages());
        pageData.put("pageNum", result.getCurrent());
        pageData.put("pageSize", result.getSize());
        
        return Result.success(pageData);
    }

    @GetMapping("/my-tasks")
    public Result<List<Map<String, Object>>> getMyTasks(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long employeeId = getCurrentEmployeeId(authHeader);
        if (employeeId == null) {
            return Result.error("无法获取用户信息");
        }

        List<Task> tasks = taskService.getTasksByEmployeeId(employeeId);
        
        List<Map<String, Object>> result = tasks.stream()
            .map(task -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", task.getId());
                map.put("taskName", task.getTaskName());
                map.put("description", task.getDescription());
                map.put("deadline", task.getDeadline() != null ? task.getDeadline().toString() : "");
                map.put("status", task.getStatus());
                map.put("progress", task.getProgress());
                map.put("priority", task.getPriority());
                map.put("creator", task.getCreator());
                map.put("createTime", task.getCreateTime());
                map.put("updateTime", task.getUpdateTime());
                return map;
            })
            .toList();

        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Task> getById(@PathVariable Long id) {
        Task task = taskService.getById(id);
        return Result.success(task);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> create(@RequestBody Map<String, Object> body) {
        try {
            Task task = new Task();
            task.setTaskName((String) body.get("taskName"));
            task.setDescription((String) body.get("description"));
            
            Long employeeId = null;
            Object employeeIdObj = body.get("employeeId");
            if (employeeIdObj instanceof Number) {
                employeeId = ((Number) employeeIdObj).longValue();
            } else if (employeeIdObj instanceof String) {
                employeeId = Long.parseLong((String) employeeIdObj);
            }
            task.setEmployeeId(employeeId);
            
            String employeeName = (String) body.get("employeeName");
            if (employeeName == null || employeeName.isEmpty()) {
                User user = userMapper.selectOne(new QueryWrapper<User>().eq("employee_id", task.getEmployeeId()));
                if (user != null) {
                    task.setEmployeeName(user.getRealName());
                } else {
                    task.setEmployeeName("未知");
                }
            } else {
                task.setEmployeeName(employeeName);
            }
            
            String deadlineStr = (String) body.get("deadline");
            if (deadlineStr != null && !deadlineStr.isEmpty()) {
                if (deadlineStr.contains("T")) {
                    deadlineStr = deadlineStr.substring(0, deadlineStr.indexOf("T"));
                }
                task.setDeadline(LocalDate.parse(deadlineStr));
            }
            
            task.setStatus("未开始");
            task.setProgress(0);
            task.setPriority((String) body.get("priority"));
            
            String creator = (String) body.get("creator");
            task.setCreator(creator != null && !creator.isEmpty() ? creator : "管理员");
            
            taskService.save(task);
            
            // 发送通知给被分配任务的员工
            if (task.getEmployeeId() != null) {
                Notification notification = new Notification();
                notification.setEmployeeId(task.getEmployeeId());
                notification.setTitle("新任务通知");
                notification.setContent("您被分配了一个新任务：" + task.getTaskName() + 
                    (task.getDeadline() != null ? "，截止日期：" + task.getDeadline() : ""));
                notification.setStatus("未读");
                notificationService.saveNotification(notification);
            }
            
            return Result.success("创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建任务失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Task task = taskService.getById(id);
        if (task == null) {
            return Result.error("任务不存在");
        }

        if (body.containsKey("taskName")) {
            task.setTaskName((String) body.get("taskName"));
        }
        if (body.containsKey("description")) {
            task.setDescription((String) body.get("description"));
        }
        if (body.containsKey("deadline")) {
            String deadlineStr = (String) body.get("deadline");
            if (deadlineStr != null && !deadlineStr.isEmpty()) {
                if (deadlineStr.contains("T")) {
                    deadlineStr = deadlineStr.substring(0, deadlineStr.indexOf("T"));
                }
                task.setDeadline(LocalDate.parse(deadlineStr));
            }
        }
        if (body.containsKey("status")) {
            task.setStatus((String) body.get("status"));
        }
        if (body.containsKey("progress")) {
            task.setProgress(((Number) body.get("progress")).intValue());
        }
        if (body.containsKey("priority")) {
            task.setPriority((String) body.get("priority"));
        }

        taskService.updateById(task);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> delete(@PathVariable Long id) {
        taskService.removeById(id);
        return Result.success("删除成功");
    }
}