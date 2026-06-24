package com.hr.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.entity.PersonnelChange;
import com.hr.mapper.PersonnelChangeMapper;
import com.hr.service.PersonnelChangeService;
import org.springframework.stereotype.Service;

@Service
public class PersonnelChangeServiceImpl extends ServiceImpl<PersonnelChangeMapper, PersonnelChange> implements PersonnelChangeService {
}
