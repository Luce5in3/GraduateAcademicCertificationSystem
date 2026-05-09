package com.graduate.certificate.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduate.certificate.common.constant.UserTypeConstant;
import com.graduate.certificate.common.context.UserContextHolder;
import com.graduate.certificate.common.exception.BusinessException;
import com.graduate.certificate.common.result.Result;
import com.graduate.certificate.entity.StudentInfo;
import com.graduate.certificate.entity.TeacherInfo;
import com.graduate.certificate.mapper.StudentInfoMapper;
import com.graduate.certificate.mapper.TeacherInfoMapper;
import com.graduate.certificate.service.OssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileUploadController {

    private final OssService ossService;
    private final StudentInfoMapper studentInfoMapper;
    private final TeacherInfoMapper teacherInfoMapper;

    /**
     * 上传用户头像
     * 上传成功后自动更新当前用户的image_url字段
     */
    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        log.info("上传头像请求, 文件名: {}, 大小: {}KB", file.getOriginalFilename(), file.getSize() / 1024);

        // 上传到OSS
        String imageUrl = ossService.uploadAvatar(file);

        // 获取当前用户信息
        Integer userType = UserContextHolder.getUserType();
        String userId = UserContextHolder.getUserId();

        // 根据用户类型更新对应表的image_url
        if (UserTypeConstant.STUDENT == userType) {
            updateStudentAvatar(userId, imageUrl);
        } else if (UserTypeConstant.TEACHER == userType) {
            updateTeacherAvatar(userId, imageUrl);
        }

        return Result.success("头像上传成功", Map.of("imageUrl", imageUrl));
    }

    /**
     * 更新学生头像
     */
    private void updateStudentAvatar(String userId, String newImageUrl) {
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentInfo::getPkUser, userId);
        StudentInfo student = studentInfoMapper.selectOne(wrapper);

        if (student == null) {
            throw new BusinessException(404, "学生信息不存在");
        }

        // 删除旧头像
        if (StringUtils.hasText(student.getImageUrl())) {
            ossService.deleteFile(student.getImageUrl());
        }

        // 更新新头像URL
        student.setImageUrl(newImageUrl);
        studentInfoMapper.updateById(student);
        log.info("更新学生头像成功: userId={}", userId);
    }

    /**
     * 更新教师头像
     */
    private void updateTeacherAvatar(String userId, String newImageUrl) {
        LambdaQueryWrapper<TeacherInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherInfo::getPkUser, userId);
        TeacherInfo teacher = teacherInfoMapper.selectOne(wrapper);

        if (teacher == null) {
            throw new BusinessException(404, "教师信息不存在");
        }

        // 删除旧头像
        if (StringUtils.hasText(teacher.getImageUrl())) {
            ossService.deleteFile(teacher.getImageUrl());
        }

        // 更新新头像URL
        teacher.setImageUrl(newImageUrl);
        teacherInfoMapper.updateById(teacher);
        log.info("更新教师头像成功: userId={}", userId);
    }
}
