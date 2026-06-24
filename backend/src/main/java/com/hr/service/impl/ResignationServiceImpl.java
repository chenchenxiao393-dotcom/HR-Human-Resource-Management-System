package com.hr.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.entity.Resignation;
import com.hr.mapper.ResignationMapper;
import com.hr.service.ResignationService;
import org.springframework.stereotype.Service;

@Service
public class ResignationServiceImpl extends ServiceImpl<ResignationMapper, Resignation> implements ResignationService {
}
