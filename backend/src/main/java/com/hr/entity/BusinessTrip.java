package com.hr.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("business_trip")
public class BusinessTrip {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long employeeId;
    private String origin;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status;
    private String approveRemark;
    private Double allowance;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
