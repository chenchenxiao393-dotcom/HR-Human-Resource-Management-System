package com.hr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hr.common.Result;
import com.hr.entity.Training;
import com.hr.entity.TrainingRecord;
import com.hr.entity.User;
import com.hr.mapper.TrainingMapper;
import com.hr.mapper.UserMapper;
import com.hr.security.JwtTokenUtil;
import com.hr.service.TrainingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employee-training")
public class EmployeeTrainingController {

    @Autowired
    private TrainingMapper trainingMapper;

    @Autowired
    private TrainingRecordService trainingRecordService;

    @Autowired
    private UserMapper userMapper;

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

    @GetMapping("/available")
    public Result<List<Map<String, Object>>> getAvailableTrainings(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long employeeId = getCurrentEmployeeId(authHeader);
        if (employeeId == null) {
            return Result.error("无法获取用户信息");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Training> allTrainings = trainingMapper.selectList(
            new QueryWrapper<Training>().eq("deleted", 0).orderByDesc("training_date")
        );

        List<TrainingRecord> enrolledRecords = trainingRecordService.getRecordsByEmployeeId(employeeId);
        List<Long> enrolledTrainingIds = enrolledRecords.stream()
            .map(TrainingRecord::getTrainingId)
            .collect(Collectors.toList());

        LocalDate today = LocalDate.now();
        
        List<Map<String, Object>> result = allTrainings.stream()
            .map(training -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", training.getId());
                map.put("trainingCode", training.getTrainingCode());
                map.put("trainingName", training.getTrainingName());
                map.put("trainingType", training.getTrainingType());
                map.put("trainingDate", training.getTrainingDate() != null ? training.getTrainingDate().format(formatter) : "");
                map.put("duration", training.getDuration());
                map.put("lecturer", training.getLecturer());
                map.put("location", training.getLocation());
                
                String status = training.getStatus();
                if ("计划中".equals(status) && training.getTrainingDate() != null && training.getTrainingDate().isBefore(today)) {
                    status = "已结束";
                }
                map.put("status", status);
                
                map.put("content", training.getContent());
                map.put("maxParticipants", training.getMaxParticipants());
                map.put("enrolled", enrolledTrainingIds.contains(training.getId()));
                return map;
            })
            .collect(Collectors.toList());

        return Result.success(result);
    }

    @PostMapping("/enroll")
    public Result<String> enrollTraining(
            @RequestBody Map<String, Long> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long employeeId = getCurrentEmployeeId(authHeader);
        if (employeeId == null) {
            return Result.error("无法获取用户信息");
        }

        Long trainingId = body.get("trainingId");
        if (trainingId == null) {
            return Result.error("请选择培训课程");
        }

        if (trainingRecordService.isEmployeeEnrolled(trainingId, employeeId)) {
            return Result.error("您已报名该培训");
        }

        Training training = trainingMapper.selectById(trainingId);
        if (training == null) {
            return Result.error("培训课程不存在");
        }

        String status = training.getStatus();
        LocalDate today = LocalDate.now();
        if ("已结束".equals(status) || ("计划中".equals(status) && training.getTrainingDate() != null && training.getTrainingDate().isBefore(today))) {
            return Result.error("该培训已结束，无法报名");
        }

        TrainingRecord record = new TrainingRecord();
        record.setTrainingId(trainingId);
        record.setEmployeeId(employeeId);
        record.setStatus("已报名");
        trainingRecordService.save(record);

        return Result.success("报名成功");
    }

    @GetMapping("/records")
    public Result<List<Map<String, Object>>> getMyTrainingRecords(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long employeeId = getCurrentEmployeeId(authHeader);
        if (employeeId == null) {
            return Result.error("无法获取用户信息");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<TrainingRecord> records = trainingRecordService.getRecordsByEmployeeId(employeeId);

        List<Map<String, Object>> result = records.stream()
            .map(record -> {
                Training training = trainingMapper.selectById(record.getTrainingId());
                Map<String, Object> map = new HashMap<>();
                map.put("id", record.getId());
                map.put("trainingId", record.getTrainingId());
                map.put("trainingCode", training != null ? training.getTrainingCode() : "");
                map.put("trainingName", training != null ? training.getTrainingName() : "");
                map.put("trainingType", training != null ? training.getTrainingType() : "");
                map.put("trainingDate", training != null && training.getTrainingDate() != null ? training.getTrainingDate().format(formatter) : "");
                map.put("duration", training != null ? training.getDuration() : 0);
                map.put("lecturer", training != null ? training.getLecturer() : "");
                map.put("location", training != null ? training.getLocation() : "");
                map.put("status", record.getStatus());
                map.put("score", record.getScore());
                map.put("evaluation", record.getEvaluation());
                map.put("attendanceStatus", record.getAttendanceStatus());
                map.put("createTime", record.getCreateTime());
                return map;
            })
            .collect(Collectors.toList());

        return Result.success(result);
    }
}
