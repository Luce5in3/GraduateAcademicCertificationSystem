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
 * 部门信息实体类
 */
@Data
@TableName("department_info")
public class DepartmentInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "pk_department", type = IdType.INPUT)
    private String pkDepartment;

    private String departmentCode;

    private String departmentName;

    private String pkCollege;

    private String leaderName;

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
