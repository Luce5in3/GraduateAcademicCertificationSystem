package com.graduate.certificate.dto.teacher;

import lombok.Data;

/**
 * 教师信息请求DTO
 * 注意：教师主键、用户ID、工号、姓名由系统从上下文获取，不允许前端传入
 */
@Data
public class TeacherInfoRequest {

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 性别：0-女 1-男
     */
    private Integer gender;

    /**
     * 所属学院
     */
    private String college;

    /**
     * 所属部门
     */
    private String department;

    /**
     * 职称：教授/副教授/讲师等
     */
    private String title;

    /**
     * 职位：院长/系主任/辅导员等
     */
    private String position;
}
