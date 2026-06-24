package com.hr.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.entity.Appraisal;
import com.hr.mapper.AppraisalMapper;
import com.hr.service.AppraisalService;
import org.springframework.stereotype.Service;

@Service
public class AppraisalServiceImpl extends ServiceImpl<AppraisalMapper, Appraisal> implements AppraisalService {
}
