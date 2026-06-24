package com.hr.service;

import com.hr.entity.Salary;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SalaryService extends IService<Salary> {
    java.util.List<Salary> getSalaryByEmployeeId(Long employeeId);
    Salary getSalaryByEmployeeAndMonth(Long employeeId, String salaryMonth);
}
