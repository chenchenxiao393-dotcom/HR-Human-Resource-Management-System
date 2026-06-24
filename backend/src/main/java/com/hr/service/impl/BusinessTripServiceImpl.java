package com.hr.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.entity.BusinessTrip;
import com.hr.mapper.BusinessTripMapper;
import com.hr.service.BusinessTripService;
import org.springframework.stereotype.Service;

@Service
public class BusinessTripServiceImpl extends ServiceImpl<BusinessTripMapper, BusinessTrip> implements BusinessTripService {
}
