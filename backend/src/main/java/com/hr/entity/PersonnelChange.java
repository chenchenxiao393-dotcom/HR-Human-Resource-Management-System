package com.hr.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("personnel_change")
public class PersonnelChange {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long employeeId;
    private String changeType;
    private String originalValue;
    private String newValue;
    private LocalDate changeDate;
    private String reason;
    private String status;
    private String approveBy;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
