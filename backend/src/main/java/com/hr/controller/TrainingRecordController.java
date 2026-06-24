package com.hr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.PageRequest;
import com.hr.common.Result;
import com.hr.entity.TrainingRecord;
import com.hr.service.TrainingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/training-record")
public class TrainingRecordController {

    @Autowired
    private TrainingRecordService trainingRecordService;

    @GetMapping
    public Result<Page<TrainingRecord>> list(PageRequest pageRequest,
                                               @RequestParam(required = false) Long employeeId,
                                               @RequestParam(required = false) Long trainingId) {
        Page<TrainingRecord> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

        QueryWrapper<TrainingRecord> queryWrapper = new QueryWrapper<>();
        if (employeeId != null) {
            queryWrapper.eq("employee_id", employeeId);
        }
        if (trainingId != null) {
            queryWrapper.eq("training_id", trainingId);
        }

        queryWrapper.orderByDesc("create_time");
        Page<TrainingRecord> result = trainingRecordService.page(page, queryWrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<TrainingRecord> getById(@PathVariable Long id) {
        TrainingRecord trainingRecord = trainingRecordService.getById(id);
        return Result.success(trainingRecord);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> create(@RequestBody TrainingRecord trainingRecord) {
        trainingRecordService.save(trainingRecord);
        return Result.success("创建成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> update(@PathVariable Long id, @RequestBody TrainingRecord trainingRecord) {
        trainingRecord.setId(id);
        trainingRecordService.updateById(trainingRecord);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> delete(@PathVariable Long id) {
        trainingRecordService.removeById(id);
        return Result.success("删除成功");
    }
}
