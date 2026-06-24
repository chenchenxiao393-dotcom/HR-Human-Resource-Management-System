package com.hr.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.entity.Overtime;
import com.hr.mapper.OvertimeMapper;
import com.hr.service.OvertimeService;
import org.springframework.stereotype.Service;

@Service
public class OvertimeServiceImpl extends ServiceImpl<OvertimeMapper, Overtime> implements OvertimeService {
}
