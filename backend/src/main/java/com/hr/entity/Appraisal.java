package com.hr.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("appraisal")
public class Appraisal {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long employeeId;
    private LocalDate appraisalDate;
    private String appraisalPeriod;
    private String appraisalType;
    private Double score;
    private String grade;
    private String evaluator;
    private String evaluation;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
