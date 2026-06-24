package com.hr.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("resignation")
public class Resignation {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long employeeId;
    private LocalDate resignationDate;
    private String resignationType;
    private String reason;
    private String handoverPerson;
    private String remark;
    private String approveStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
