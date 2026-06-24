package com.hr.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("salary")
public class Salary {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long employeeId;
    private String salaryMonth;
    private BigDecimal baseSalary;
    private BigDecimal performanceSalary;
    private BigDecimal bonus;
    private BigDecimal overtimePay;
    private BigDecimal allowance;
    private BigDecimal otherIncome;
    private BigDecimal socialInsurance;
    private BigDecimal housingFund;
    private BigDecimal incomeTax;
    private BigDecimal otherDeduction;
    private BigDecimal grossSalary;
    private BigDecimal netSalary;
    private String status;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
