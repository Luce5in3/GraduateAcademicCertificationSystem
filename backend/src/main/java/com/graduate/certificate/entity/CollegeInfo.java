package com.graduate.certificate.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学院信息实体类
 */
@Data
@TableName("college_info")
public class CollegeInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "pk_college", type = IdType.INPUT)
    private String pkCollege;

    private String collegeCode;

    private String collegeName;

    private String deanName;

    private String phone;

    private String officeAddr;

    private Integer sortOrder;

    private Integer isActive;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
