package com.hr.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("training")
public class Training {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String trainingCode;
    private String trainingName;
    private String trainingType;
    private LocalDate trainingDate;
    private Integer duration;
    private String lecturer;
    private String location;
    private String content;
    private Integer maxParticipants;
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
