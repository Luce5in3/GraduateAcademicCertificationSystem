package com.graduate.certificate.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduate.certificate.common.result.Result;
import com.graduate.certificate.dto.teacher.TeacherInfoRequest;
import com.graduate.certificate.dto.teacher.TeacherInfoResponse;
import com.graduate.certificate.service.TeacherInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 教师信息控制器
 */
@Slf4j
@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherInfoController {

    private final TeacherInfoService teacherInfoService;

    /**
     * 更新教师信息（仅当前登录的教师可更新自己的信息）
     */
    @PutMapping
    public Result<TeacherInfoResponse> updateTeacher(@RequestBody TeacherInfoRequest request) {
        log.info("更新教师信息请求: {}", request);
        TeacherInfoResponse response = teacherInfoService.updateTeacher(request);
        return Result.success("更新教师信息成功", response);
    }

    /**
     * 获取当前登录教师的信息
     */
    @GetMapping("/current")
    public Result<TeacherInfoResponse> getCurrentTeacher() {
        log.info("获取当前教师信息");
        // 从上下文获取当前用户ID
        String userId = com.graduate.certificate.common.context.UserContextHolder.getUserId();
        TeacherInfoResponse response = teacherInfoService.getTeacherByUserId(userId);
        return Result.success(response);
    }

    /**
     * 删除教师信息
     */
    @DeleteMapping("/{pkTeacher}")
    public Result<Void> deleteTeacher(@PathVariable String pkTeacher) {
        log.info("删除教师信息请求: {}", pkTeacher);
        teacherInfoService.deleteTeacher(pkTeacher);
        return Result.success("删除教师信息成功");
    }

    /**
     * 根据主键查询教师信息
     */
    @GetMapping("/{pkTeacher}")
    public Result<TeacherInfoResponse> getTeacherById(@PathVariable String pkTeacher) {
        log.info("查询教师信息请求: {}", pkTeacher);
        TeacherInfoResponse response = teacherInfoService.getTeacherById(pkTeacher);
        return Result.success(response);
    }

    /**
     * 根据用户ID查询教师信息
     */
    @GetMapping("/user/{pkUser}")
    public Result<TeacherInfoResponse> getTeacherByUserId(@PathVariable String pkUser) {
        log.info("根据用户ID查询教师信息: {}", pkUser);
        TeacherInfoResponse response = teacherInfoService.getTeacherByUserId(pkUser);
        return Result.success(response);
    }

    /**
     * 根据工号查询教师信息
     */
    @GetMapping("/teacherNo/{teacherNo}")
    public Result<TeacherInfoResponse> getTeacherByTeacherNo(@PathVariable String teacherNo) {
        log.info("根据工号查询教师信息: {}", teacherNo);
        TeacherInfoResponse response = teacherInfoService.getTeacherByTeacherNo(teacherNo);
        return Result.success(response);
    }

    /**
     * 分页查询教师信息
     */
    @GetMapping("/page")
    public Result<IPage<TeacherInfoResponse>> pageTeachers(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String college,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer approvalLevel) {
        log.info("分页查询教师信息: current={}, size={}, college={}, department={}, approvalLevel={}", 
                current, size, college, department, approvalLevel);
        IPage<TeacherInfoResponse> page = teacherInfoService.pageTeachers(current, size, college, department, approvalLevel);
        return Result.success(page);
    }
}
