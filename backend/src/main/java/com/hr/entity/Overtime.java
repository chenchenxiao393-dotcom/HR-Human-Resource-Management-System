package com.hr.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("overtime")
public class Overtime {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long employeeId;
    private LocalDate overtimeDate;
    private String startTime;
    private String endTime;
    private Double hours;
    private String reason;
    private String status;
    private String approveRemark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
