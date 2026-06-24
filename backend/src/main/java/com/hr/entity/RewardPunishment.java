package com.hr.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("reward_punishment")
public class RewardPunishment {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long employeeId;
    private String type;
    private String reason;
    private Double amount;
    private LocalDate rpDate;
    private String status;
    private String approveBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
