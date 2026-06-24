package com.hr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.PageRequest;
import com.hr.common.Result;
import com.hr.entity.Training;
import com.hr.entity.TrainingRecord;
import com.hr.service.TrainingRecordService;
import com.hr.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/training")
public class TrainingController {

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private TrainingRecordService trainingRecordService;

    @GetMapping
    public Result<Page<Training>> list(PageRequest pageRequest,
                                        @RequestParam(required = false) String trainingName,
                                        @RequestParam(required = false) String trainingType,
                                        @RequestParam(required = false) String status) {
        Page<Training> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

        QueryWrapper<Training> queryWrapper = new QueryWrapper<>();
        if (trainingName != null && !trainingName.isEmpty()) {
            queryWrapper.like("training_name", trainingName);
        }
        if (trainingType != null && !trainingType.isEmpty()) {
            queryWrapper.eq("training_type", trainingType);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByDesc("training_date", "create_time");
        Page<Training> result = trainingService.page(page, queryWrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Training> getById(@PathVariable Long id) {
        Training training = trainingService.getById(id);
        return Result.success(training);
    }

    @GetMapping("/all")
    public Result<List<Training>> getAll() {
        return Result.success(trainingService.list());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> create(@RequestBody Training training) {
        trainingService.save(training);
        return Result.success("创建成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> update(@PathVariable Long id, @RequestBody Training training) {
        training.setId(id);
        trainingService.updateById(training);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> delete(@PathVariable Long id) {
        Training training = trainingService.getById(id);
        if (training == null) {
            return Result.error("培训不存在");
        }

        String status = training.getStatus();
        LocalDate today = LocalDate.now();
        if ("已结束".equals(status) || ("计划中".equals(status) && training.getTrainingDate() != null && training.getTrainingDate().isBefore(today))) {
            return Result.error("已结束的培训不能删除");
        }

        QueryWrapper<TrainingRecord> recordQuery = new QueryWrapper<>();
        recordQuery.eq("training_id", id).eq("deleted", 0);
        long recordCount = trainingRecordService.count(recordQuery);
        if (recordCount > 0) {
            return Result.error("已有员工报名该培训，无法删除");
        }

        trainingService.removeById(id);
        return Result.success("删除成功");
    }
}
