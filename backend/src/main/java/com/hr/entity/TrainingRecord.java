package com.hr.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("training_record")
public class TrainingRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long trainingId;
    private Long employeeId;
    private String status;
    private BigDecimal score;
    private String evaluation;
    private String attendanceStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
