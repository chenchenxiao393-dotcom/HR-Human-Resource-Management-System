package com.hr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.entity.Salary;
import com.hr.mapper.SalaryMapper;
import com.hr.service.SalaryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaryServiceImpl extends ServiceImpl<SalaryMapper, Salary> implements SalaryService {

    @Override
    public List<Salary> getSalaryByEmployeeId(Long employeeId) {
        QueryWrapper<Salary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employee_id", employeeId);
        queryWrapper.eq("deleted", 0);
        queryWrapper.orderByDesc("salary_month");
        return list(queryWrapper);
    }

    @Override
    public Salary getSalaryByEmployeeAndMonth(Long employeeId, String salaryMonth) {
        QueryWrapper<Salary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employee_id", employeeId);
        queryWrapper.eq("salary_month", salaryMonth);
        queryWrapper.eq("deleted", 0);
        return getOne(queryWrapper);
    }
}
