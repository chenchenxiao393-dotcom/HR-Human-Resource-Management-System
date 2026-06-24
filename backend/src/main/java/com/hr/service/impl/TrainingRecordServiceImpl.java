package com.hr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.entity.TrainingRecord;
import com.hr.mapper.TrainingRecordMapper;
import com.hr.service.TrainingRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingRecordServiceImpl extends ServiceImpl<TrainingRecordMapper, TrainingRecord> implements TrainingRecordService {

    @Override
    public List<TrainingRecord> getRecordsByEmployeeId(Long employeeId) {
        QueryWrapper<TrainingRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employee_id", employeeId);
        queryWrapper.eq("deleted", 0);
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }

    @Override
    public TrainingRecord getRecordByTrainingAndEmployee(Long trainingId, Long employeeId) {
        QueryWrapper<TrainingRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("training_id", trainingId);
        queryWrapper.eq("employee_id", employeeId);
        queryWrapper.eq("deleted", 0);
        return getOne(queryWrapper);
    }

    @Override
    public boolean isEmployeeEnrolled(Long trainingId, Long employeeId) {
        QueryWrapper<TrainingRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("training_id", trainingId);
        queryWrapper.eq("employee_id", employeeId);
        queryWrapper.eq("deleted", 0);
        return count(queryWrapper) > 0;
    }
}
