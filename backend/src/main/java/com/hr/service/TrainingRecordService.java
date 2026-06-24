package com.hr.service;

import com.hr.entity.TrainingRecord;
import com.baomidou.mybatisplus.extension.service.IService;

public interface TrainingRecordService extends IService<TrainingRecord> {
    java.util.List<TrainingRecord> getRecordsByEmployeeId(Long employeeId);
    TrainingRecord getRecordByTrainingAndEmployee(Long trainingId, Long employeeId);
    boolean isEmployeeEnrolled(Long trainingId, Long employeeId);
}
