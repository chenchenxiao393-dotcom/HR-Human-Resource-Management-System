package com.hr.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.entity.Training;
import com.hr.mapper.TrainingMapper;
import com.hr.service.TrainingService;
import org.springframework.stereotype.Service;

@Service
public class TrainingServiceImpl extends ServiceImpl<TrainingMapper, Training> implements TrainingService {
}
