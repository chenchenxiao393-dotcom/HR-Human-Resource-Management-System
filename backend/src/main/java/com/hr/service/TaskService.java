package com.hr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hr.entity.Task;

import java.util.List;

public interface TaskService extends IService<Task> {
    List<Task> getTasksByEmployeeId(Long employeeId);
}